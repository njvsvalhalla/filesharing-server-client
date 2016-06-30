import { hash } from '../lib/hash'

const createUser = (username, password) => {
  return {
    'username': username,
    'passhash': hash(password)
  }
}

export default createUser
