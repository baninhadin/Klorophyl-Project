const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
  userName: {
    type: String,
    required: true,
    unique: true,
  },
  password: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    required: true,
    unique: true,
  },
  location: {
    type: String,
    required: true,
  },
  fullName: {
    type: String,
    required: true,
  },
  points: {
    type: Number,
    default: 0,
  },
  avatar: {
    type: String,
    default: 'https://hidden-will-313103.uc.r.appspot.com/uploads/default.png',
  },
  createDate: {
    type: Date,
    required: true,
    default: new Date(),
  },
})

module.exports = mongoose.model('user', userSchema)
