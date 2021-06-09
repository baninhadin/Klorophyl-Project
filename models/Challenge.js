const mongoose = require('mongoose')

const challengeSchema = new mongoose.Schema({
  name: {
    type: String,
  }
  location: {
    type: String,
    required: true,
  },
  challenge_co: {
    description: {
      type: String,
      default:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed diam lectus, pretium eget facilisis ac, lobortis quis tortor. Cras ac quam elit. Duis luctus turpis a justo sodales porttitor.',
    },
    points: {
      type: Number,
      default: 50,
    },
    qrcode: {
      type: String,
    },
  },
  challenge_pm10: {
    description: {
      type: String,
      default:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed diam lectus, pretium eget facilisis ac, lobortis quis tortor. Cras ac quam elit. Duis luctus turpis a justo sodales porttitor.',
    },
    points: {
      type: Number,
      default: 50,
    },
    qrcode: {
      type: String,
    },
  },
  challenge_o3: {
    description: {
      type: String,
      default:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed diam lectus, pretium eget facilisis ac, lobortis quis tortor. Cras ac quam elit. Duis luctus turpis a justo sodales porttitor.',
    },
    points: {
      type: Number,
      default: 50,
    },
    qrcode: {
      type: String,
    },
  },
  challenge_so2: {
    description: {
      type: String,
      default:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed diam lectus, pretium eget facilisis ac, lobortis quis tortor. Cras ac quam elit. Duis luctus turpis a justo sodales porttitor.',
    },
    points: {
      type: Number,
      default: 50,
    },
    qrcode: {
      type: String,
    },
  },
  challenge_no2: {
    description: {
      type: String,
      default:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed diam lectus, pretium eget facilisis ac, lobortis quis tortor. Cras ac quam elit. Duis luctus turpis a justo sodales porttitor.',
    },
    points: {
      type: Number,
      default: 50,
    },
    qrcode: {
      type: String,
    },
  },
  date: {
    type: String,
    required: true,
    default: new Date().toJSON().slice(0, 10),
  },
})

module.exports = mongoose.model('challenge', challengeSchema)
