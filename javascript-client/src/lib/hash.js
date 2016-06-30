import bcrypt from 'bcryptjs'

const hash = (password) => {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(10))
}

const compareHash = (password, hash) => {
  return bcrypt.compareSync(password, hash)
}

export {
  hash,
  compareHash
}
