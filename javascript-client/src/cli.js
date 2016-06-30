import vorpal from 'vorpal'
import net from 'net'
import createUser from './model/user'

const cli = vorpal()

let loggedin = false // when not logged in, it will be false, when logged in it will take on the username

let server
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

cli
  .command('login <username> <password>')
  .description('Logs you in')
  .action((args, callback) => {
    connectToServer()
    writeTo(`passhashget ${args.username}`)
    server.on('data', (d) => {
      if (d.toString().localeCompare(args.password) === 0) {
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
  .action(() => {
    if (!loggedin) {
      cli.log('You are not logged in. You have to be logged in to log out.')
    } else {
      loggedin = false
    }
  })

cli
  .command('amiloggedin')
  .description('Tells if you are logged in and what user you are logged in as')
  .action((args,callback) => {
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

cli
  .command('upload <filepath> [filepath]')
  .description('Upload a file to you')

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
