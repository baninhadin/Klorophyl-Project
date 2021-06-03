const mongoose = require('mongoose')

const ProfileSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'user',
  },
  location: {
    type: String,
    required: true,
  },
  fullName: {
    type: String,
    required: true,
  },
  date: {
    type: Date,
    default: new Date(),
  },
  points: {
    type: Number,
    default: 0,
  },
  avatar: {
    type: String,
    default: 'https://hidden-will-313103.uc.r.appspot.com/uploads/default.png',
  },
})

module.exports = mongoose.model('profile', ProfileSchema)
