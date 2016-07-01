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

const writeTo = (string) => server.write(string + '\n')

const writeJSONUser = (object) => {
  server.write(JSON.stringify({ 'user': object }) + '\n')
}

const writeJSONFile = (object) => {
  server.write(JSON.stringify({ 'files': object }) + '\n')
}

/* Login command
    Takes a username, password, and authenticates the hash, if successful
    we log in
*/
cli
  .command('login <username> <password>')
  .description('Logs you in')
  .action((args, callback) => {
    /*
    let a = hash(args.password)
    let hashTo
    a.then((hashed) => hashTo = hashed)
    .then(() => connectToServer())
    .then(() => writeJSONUser(createUser(args.username, hashTo)))
    .then(() => closeConnection())
    .then(() => cli.log('You successfully registered!'))
    .catch((err) => cli.log(`There was an error when registering: ${err}`))
    */
    connectToServer()
    writeTo(`passhashget ${args.username}`)
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
    .then(() => writeJSONUser(createUser(args.username, hashTo)))
    .then(() => closeConnection())
    .then(() => cli.log('You successfully registered!'))
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
    } else {
      connectToServer()
      writeTo(`getfile ${args.fileid}`)
      server.on('data', (d) => {
        let filePathToSave
        let parsed = JSON.parse((d.toString()))
        if (!args.filepath) {
          filePathToSave = parsed.files.filePath
        } else {
          filePathToSave = args.filepath
        }
        let buffer = Buffer.from(parsed.files.buffer, 'base64')
        fs.open(filePathToSave, 'w', (err, fd) => {
          if (err) cli.log(`Error opening file to write file: ${err}`)
          fs.write(fd, buffer, 0, buffer.length, null, (err) => {
            if (err) cli.log(`Error writing to file: ${err}`)
            fs.close(fd, () => {
              cli.log(`File written to ${filePathToSave}!`)
            })
          })
        })
      })
    }
    closeConnection()
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
    } else {
      let filePathToUpload
      if (!args.pathfordatabase) {
        filePathToUpload = args.absolutefilepath
      } else {
        filePathToUpload = args.pathfordatabase
      }
      connectToServer()
      fs.open(args.absolutefilepath, 'r', (err, fd) => {
        if (err) {
          cli.log(`There was an error opening the file to send: ${err}`)
        }
        buffer = new Buffer.alloc(fs.statSync(args.absolutefilepath).size)
        fs.read(fd, buffer, 0, buffer.length, 0, (err, num) => {
          if (err) {
            cli.log(`There was an error opening the file to send: ${err}`)
          }
          writeJSONFile(createFile(filePathToUpload, buffer.toString('base64'), loggedin))
          if (err) throw err
        })
      })
    }
    closeConnection()
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
    } else {
      connectToServer()
      writeTo(`getlist ${loggedin}`)
      server.on('data', (d) => {
        cli.log(d.toString())
      })
      closeConnection()
      callback()
    }
  })

export default cli
