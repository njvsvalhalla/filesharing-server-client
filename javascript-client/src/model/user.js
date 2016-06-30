//import { hash } from './hash'

const createUser = (username, passhash) => {
  return {
    'username': username,
    'passhash': passhash
  }
}

export default createUser
