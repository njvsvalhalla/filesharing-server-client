const createUser = (username, password) => {
  return {
    'username': username,
    'passhash': password
  }
}

export default createUser
