import bcrypt from 'bcryptjs'

const hash = (password) => {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(10))
}

export {
  hash
}
