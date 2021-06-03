const express = require('express')
const router = express.Router()
const bcrypt = require('bcryptjs')

const simpleHash = (str) => {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = (hash << 5) - hash + char
    hash &= hash // Convert to 32bit integer
  }
  return new Uint32Array([hash])[0].toString(36)
}

function translateLocation(location) {
  if (location == 'South') return 'Jakarta South (US Consulate), Indonesia'
  if (location == 'Central') return 'Jakarta Central (US Consulate), Indonesia'
  if (location == 'Jakarta') return 'Jakarta, Indonesia'
  return null
}

function isChallengeAvail(chall) {
  if (chall == 'challenge_co') return true
  if (chall == 'challenge_pm10') return true
  if (chall == 'challenge_o3') return true
  if (chall == 'challenge_so2') return true
  if (chall == 'challenge_no2') return true

  return false
}

const Challenge = require('../../models/Challenge')

// Getting current challenge based on location
router.get('/:locationName', async (req, res) => {
  let nowDate = new Date()
  const salt = await bcrypt.genSalt(10)

  let hash = await bcrypt.hash('challenge_co', salt)
  //   console.log(simpleHash('challenge_co'))
  try {
    let realLocation = translateLocation(req.params.locationName)

    if (realLocation) {
      const challenge = await Challenge.find({
        date: nowDate.toJSON().slice(0, 10),
        location: realLocation,
      }).select({ _id: 0, __v: 0 })

      return res.json(challenge)
    }
    return res.json({ message: 'Location not available' })
  } catch (err) {
    res.status(500).json({ message: err.message })
  }
})

// Getting current challenge based on challange name in location
router.get('/:locationName/:challengeName', async (req, res) => {
  let nowDate = new Date()
  try {
    let realLocation = translateLocation(req.params.locationName)

    if (realLocation) {
      if (isChallengeAvail(req.params.challengeName)) {
        const challenge = await Challenge.find({
          date: nowDate.toJSON().slice(0, 10),
          location: realLocation,
        }).select({ _id: 0, __v: 0 })
        return res.json(challenge[0][req.params.challengeName])
      }

      return res.json({ message: 'Challenge not available' })
    }
    return res.json({ message: 'Location not available' })
  } catch (err) {
    res.status(500).json({ message: err.message })
  }
})

// Getting all history challenge
router.get('/history', async (req, res) => {
  try {
    const challenge = await Challenge.find()
    res.json(challenge)
  } catch (err) {
    res.status(500).json({ message: err.message })
  }
})

// post hasil predict dan save di database
router.post('/', async (req, res) => {
  const nowDate = new Date().toJSON().slice(0, 10)
  //   console.log(translateLocation(req.body.Location))
  console.log(nowDate)
  try {
    let challenge = await Challenge.findOne({
      location: translateLocation(req.body.Location),
      date: nowDate,
    })

    if (Object.keys(req.body).length == 0) {
      return res.status(500).json({ message: 'Need Body Data' })
    }

    const {
      Location,
      challenge_co,
      challenge_pm10,
      challenge_o3,
      challenge_so2,
      challenge_no2,
    } = req.body

    // Translating location
    let realLocation = translateLocation(Location)

    const challengeAttr = {
      challenge_co: {
        points: challenge_co,
        qrcode: `https://hidden-will-313103.uc.r.appspot.com/${Location}/challenge_co`,
      },
      challenge_pm10: {
        points: challenge_pm10,
        qrcode: `https://hidden-will-313103.uc.r.appspot.com/${Location}/challenge_pm10`,
      },
      challenge_o3: {
        points: challenge_o3,
        qrcode: `https://hidden-will-313103.uc.r.appspot.com/${Location}/challenge_o3`,
      },
      challenge_so2: {
        points: challenge_so2,
        qrcode: `https://hidden-will-313103.uc.r.appspot.com/${Location}/challenge_so2`,
      },
      challenge_no2: {
        points: challenge_no2,
        qrcode: `https://hidden-will-313103.uc.r.appspot.com/${Location}/challenge_no2`,
      },
    }

    if (challenge) {
      challenge = await Challenge.findOneAndUpdate(
        { date: nowDate },
        { $set: challengeAttr },
        { new: true, upsert: true, setDefaultsOnInsert: true }
      )
      return res.json({ message: 'Data updated' })
    }

    challenge = new Challenge({
      date: nowDate,
      location = realLocation,
      ...challengeAttr,
    })

    await challenge.save()

    return res.json({ message: 'Data saved to database' })
  } catch (err) {
    console.error(err.message)
    res.status(500).send('Server error')
  }
})

module.exports = router
