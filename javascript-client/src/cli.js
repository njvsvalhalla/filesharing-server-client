/* imports */
import vorpal from 'vorpal'
import net from 'net'
import fs from 'fs'

import createUser from './model/user'
import createFile from './model/File'
import { hash, compareHash } from './lib/hash'

/* constant variables */
const cli = vorpal()
const host = 'localhost'
const port = 667

/* initialize variables */
let loggedin = false // when not logged in, it will be false, when logged in it will take on the username
let server
let buffer

/* Server functions to connect, close, and write to our server */
const connectToServer = () => {
  server = net.createConnection(port, host, () => {
    return 0
  })
}

const closeConnection = () => {
  server.end()
}
/*
  The write functions
  writeTo writes just a string to the socket
  writeJSONUser sends a JSON object for the user to the server
  writeJSONFile sends a JSON object for the file to the server
*/
// const writeTo = (string) => server.write(string + '\n')

const writeJSON = (type, object) => {
  if (type === 'user') {
    server.write(JSON.stringify({ 'user': object }) + '\n')
  } else if (type === 'file') {
    server.write(JSON.stringify({ 'files': object }) + '\n')
  } else if (type === 'gethash') {
    server.write(JSON.stringify({ 'Message': object }) + '\n')
  }else if (type === 'getfiles') {
    server.write(JSON.stringify({ 'Message': object }) + '\n')
  }
}

/* Login command
    Takes a username, password, and authenticates the hash, if successful
    we log in
*/
cli
  .command('login <username> <password>')
  .description('Logs you in')
  .action((args, callback) => {
    connectToServer()
    //  writeTo(`passhashget ${args.username}`)
    let userObj = {
      'username': args.username,
      'command': 'gethash'
    }
    writeJSON('gethash', userObj)
    server.on('data', (d) => {
      let hashServer = d.toString()
      let comparing = compareHash(args.password, hashServer)
      comparing.then((res) => {
        if (res) {
          loggedin = args.username
          cli.log(`You have now logged in as ${loggedin}`)
        } else {
          cli.log('Please check your username or password again.')
        }
      })
    })
    closeConnection()
    callback()
  })

/* Logout command
  If you are logged in, it logs you out
*/
cli
  .command('logout')
  .description('Logs you out, if you are logged in')
  .action((callback) => {
    if (!loggedin) {
      cli.log('You are not logged in. You have to be logged in to log out.')
    } else {
      loggedin = false
    }
    callback()
  })

/* amiloggedin
    If you are logged in, it tells you your username
*/
cli
  .command('amiloggedin')
  .description('Tells if you are logged in and what user you are logged in as')
  .action((args, callback) => {
    if (!loggedin) {
      cli.log('You are not logged in. Please register or log in.')
    } else {
      cli.log(`You are logged in as ${loggedin}! If you would like to log out, type in logout`)
    }
    callback()
  })
/* Register command
    Takes your username and password, and registers with the server
*/
cli
  .command('register <username> <password>')
  .description('Register your user to our database')
  .action((args, callback) => {
    let a = hash(args.password)
    let hashTo
    a.then((hashed) => hashTo = hashed)
    .then(() => connectToServer())
    .then(() => writeJSON('user', createUser(args.username, hashTo)))
    .then(() => {
      server.on('data', (d) => {
        cli.log(d.toString())
        closeConnection()
        callback()
      })
    })
    .catch((err) => cli.log(`There was an error when registering: ${err}`))
  })

/* Download file
    Takes a fileId, if the user is logged in, and downloads it. If a path is specified,
    it saves it to that, otherwise takes the pathname from the database
*/

cli
  .command('download <fileid> [filepath]')
  .description('Downloads a file from your database')
  .action((args, callback) => {
    if (!loggedin) {
      cli.log('Sorry if you wish to use this command, please try to log in!')
      callback()
    } else {
      connectToServer()
      let userObj = {
        'username': loggedin,
        'command': 'download',
        'fileid': args.fileid
      }
      writeJSON('getfiles', userObj)
      // writeTo(`getfile ${args.fileid} ${loggedin}`)
      server.on('data', (d) => {
        if (d.toString() === 'You aren\'t the owner to that file!') {
          cli.log(d.toString())
          closeConnection()
        } else {
          let filePathToSave
          let parsed = JSON.parse((d.toString()))
          if (!args.filepath) {
            filePathToSave = parsed.files.filePath
          } else {
            filePathToSave = args.filepath
          }
          let buffer = Buffer.from(parsed.files.buffer, 'base64')
          fs.open(filePathToSave, 'w', (err, fd) => {
            if (err) {
              cli.log(`Error opening file to write file: ${err}`)
              callback()
            }
            fs.write(fd, buffer, 0, buffer.length, null, (err) => {
              if (err) {
                cli.log(`Error writing to file: ${err}`)
                callback()
              }
              fs.close(fd, () => {
                cli.log(`File written to ${filePathToSave}!`)
              })
            })
          })
        }
      })
    }
    callback()
  })
/* Upload command
    takes a file path from the local machine, and if the user wants to save it somewhere else, that gets passed in.
    Converts the file to a base64 string and passed to the server for storage
*/
cli
  .command('upload <absolutefilepath> [pathfordatabase]')
  .description('Upload a file to you')
  .action((args, callback) => {
    if (!loggedin) {
      cli.log('Sorry if you wish to use this command, please try to log in!')
      callback()
    } else {
      let filePathToUpload
      if (!args.pathfordatabase) {
        filePathToUpload = args.absolutefilepath
      } else {
        filePathToUpload = args.pathfordatabase
      }
      connectToServer()
      server.on('data', (d) => {
        cli.log(d.toString())
        closeConnection() //  if we get data then we know we successfully have the file stored or it messed up
      })
      fs.open(args.absolutefilepath, 'r', (err, fd) => {
        if (err) {
          cli.log(`There was an error opening the file to send: ${err}`)
          callback()
        }
        buffer = new Buffer.alloc(fs.statSync(args.absolutefilepath).size)
        fs.read(fd, buffer, 0, buffer.length, 0, (err, num) => {
          if (err) {
            cli.log(`There was an error opening the file to send: ${err}`)
            callback()
          }
          writeJSON('file', createFile(filePathToUpload, buffer.toString('base64'), loggedin))
          if (err) throw err
        })
      })
    }
    callback()
  })

/* Files command
    List files for specified user, if logged in
*/

cli
  .command('files')
  .description('Retrieve list of files')
  .action((args, callback) => {
    if (!loggedin) {
      cli.log('Sorry if you wish to use this command, please try to log in!')
      callback()
    } else {
      connectToServer()
      let userObj = {
        'username': loggedin,
        'command': 'getfiles'
      }
      writeJSON('gethash', userObj)
      cli.log('ID |  Path')
      server.on('data', (d) => {
        cli.log(d.toString())
      })
      closeConnection()
      callback()
    }
  })

export default cli
