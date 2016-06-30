import net from 'net'
//import { compare } from './hash'
import createUser from '../model/user'

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

const registerUser = (username, password) => {
  console.log('connecting to server')
  connectToServer()
  writeJSONUser(createUser(username, password))
  closeConnection()
}

const loginUser = (username, password) => {
  connectToServer()
  writeTo(`passhashget ${username}`)
  server.on('data', (d) => {
    d.toString().localeCompare(password)
  })
  closeConnection()
}

const downloadFile = (username, id, path) => {
  connectToServer()
  writeTo(`DOWNLOAD ${username} ${id} ${path}`)
  closeConnection()
}

const sendFile = (username, path, secondpath) => {
  connectToServer()
  writeTo(`SENDINGFILE ${username} ${secondpath}`)
  writeTo(`THIS IS WHERE B64 FILE DATA WOULD SEND`)
  closeConnection()
}

const fileList = (username) => {
  connectToServer()
  writeTo(`getlist ${username}`)
  server.on('data', (d) => {
    console.log(d.toString()+ '\n')
  })
  closeConnection()
}

export {
  registerUser,
  loginUser,
  downloadFile,
  sendFile,
  fileList
}
