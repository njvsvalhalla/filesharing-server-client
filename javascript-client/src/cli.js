import vorpal from 'vorpal'
import net from 'net'
import fs from 'fs'
import createUser from './model/user'
import createFile from './model/File'
import bcrypt from 'bcryptjs'

const compareHash = (password, hash) => {
  return bcrypt.compareSync(password, hash)
}

const cli = vorpal()

let loggedin = 'neald' // when not logged in, it will be false, when logged in it will take on the username

let server

let buffer
const port = 667
const host = 'localhost'

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
//  server.write(JSON.stringify(format(JSON.stringify({ 'files': object }), 'type')) + '\n')
  server.write(JSON.stringify({ 'files': object }) + '\n')
}

cli
  .command('login <username> <password>')
  .description('Logs you in')
  .action((args, callback) => {
    connectToServer()
    writeTo(`passhashget ${args.username}`)
    server.on('data', (d) => {
      if (compareHash(args.password, d.toString())) {
        loggedin = args.username
        cli.log(`You have now logged in as ${loggedin}`)
      } else {
        cli.log('Something went wrong logging in :(')
      }
    })
    closeConnection()
    callback()
  })

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

cli
  .command('register <username> <password>')
  .description('Register your user to our database')
  .action((args, callback) => {
    connectToServer()
    writeJSONUser(createUser(args.username, args.password))
    closeConnection()
    callback()
  })

cli
  .command('download <fileid> [filepath]')
  .description('Downloads a file from your database')
  .action((args, callback) => {
    connectToServer()
    writeTo(`getfile ${args.fileid}`)
    server.on('data', (d) => {
      let parsed = JSON.parse((d.toString()))
      let filePathToSave = parsed.files.filePath
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
      closeConnection()
      callback()
    })
  })
cli
  .command('upload <absolutefilepath> [pathfordatabase]')
  .description('Upload a file to you')
  .action((args, callback) => {
    connectToServer()
    fs.open(args.absolutefilepath, 'r', function (status, fd) {
      if (status) {
        console.log(status.message)
        return
      }
      buffer = new Buffer.alloc(fs.statSync(args.absolutefilepath).size)
      fs.read(fd, buffer, 0, buffer.length, 0, function (err, num) {
        console.log(buffer.toString('base64'))
        writeJSONFile(createFile(args.absolutefilepath, buffer.toString('base64'), loggedin))
        if (err) throw err
      })
    })
  //  closeConnection()
    callback()
  })

cli
  .command('files')
  .description('Retrieve list of files')
  .action((args, callback) => {
    connectToServer()
    writeTo(`getlist ${loggedin}`)
    server.on('data', (d) => {
      cli.log(d.toString())
    })
    closeConnection()
    callback()
  })

export default cli
