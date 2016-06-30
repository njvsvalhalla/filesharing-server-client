import vorpal from 'vorpal'
import { testConnection, registerUser, loginUser, downloadFile, sendFile, fileList } from './lib/connect.js'

const cli = vorpal()

let loggedin = 'testingfornow' // when not logged in, it will be false, when logged in it will take on the username

/*
  Command: Login
  Params: <username> <password>
  Function: Logs the user in, uses loginUser from lib/connect
*/
cli
  .command('login <username> <password>')
  .description('Logs you in')
  .action((args, callback) => {
    loginUser(args.username, args.password)
    // if (loginUser(args.username, args.password) === 0) {
    //   loggedin = args.username
    //   cli.log(`You have now logged in as ${loggedin}`)
    // } else {
    //   cli.log('Something went wrong try again')
    // }
    callback()
  })

/*
  Command: Logout
  Function: Logs the user out, if you are logged in
*/
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

/*
  Command: amiloggedin
  Function: Tells if you are logged in and what user you are logged in as
*/

cli
  .command('amiloggedin')
  .description('Tells if you are logged in and what user you are logged in as')
  .action(() => {
    if (!loggedin) {
      cli.log('You are not logged in. Please register or log in.')
    } else {
      cli.log(`You are logged in as ${loggedin}! If you would like to log out, type in logout`)
    }
  })

/*
  Command: register
  Params: <username> <password>
  Function: Creates a User object, which is then passed into registerUser in lib/connect
*/
cli
  .command('register <username> <password>')
  .description('Register your user to our database')
  .action((args, callback) => {
    registerUser(args.username,args.password)
    callback()
  })

/*
  Command: download
  Params: <fileid> [filepath]
  Function: Downloads a file to the path specified by the database, or to another path if one is specified
*/
cli
  .command('download <fileid> [filepath]')
  .description('Downloads a file from your database')

/*
  Command: upload
  Params: <filepath> [filepath]
  Function: Encrypts file, and saves it to the database
*/
cli
  .command('upload <filepath> [filepath]')
  .description('Upload a file to you')

/*
  Command: files
  Function: Gets a list of files for the user
*/
cli
  .command('files')
  .description('Retrieve list of files')
  .action((args, callback) => {
    fileList(loggedin)
    callback()
  })

export default cli
