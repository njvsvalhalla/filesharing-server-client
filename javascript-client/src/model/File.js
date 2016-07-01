const createFile = (filepath, buffer, username) => {
  return {
    'filePath': filepath,
    'buffer': buffer,
    'username': username
  }
}

export default createFile
