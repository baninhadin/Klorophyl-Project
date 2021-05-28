const mongoose = require('mongoose')

const dataSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  geo: {
    latitude: {
      type: String,
      required: true,
    },
    longitude: {
      type: String,
      required: true,
    },
  },
  weather: {
    humidity: {
      type: Number,
    },
    windDir: {
      type: String,
    },
    windSpeed: {
      type: Number,
    },
    temp: {
      type: Number,
    },
    precip: {
      type: Number,
    },
    desc: {
      type: String,
    },
  },
  airQuality: {
    aqi: {
      type: Number,
    },
    co: {
      type: Number,
    },
    o3: {
      type: Number,
    },
    so2: {
      type: Number,
    },
    no2: {
      type: Number,
    },
    pm10: {
      type: Number,
    },
    pm25: {
      type: Number,
    },
  },
  date: {
    type: Date,
    required: true,
    default: Date.now,
  },
})

module.exports = mongoose.model('data', dataSchema)
