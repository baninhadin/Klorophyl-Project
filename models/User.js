const mongoose = require('mongoose');

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

  createDate: {
    type: Date,
    required: true,
    default: Date.now,
  },
});

module.exports = mongoose.model('user', userSchema);
