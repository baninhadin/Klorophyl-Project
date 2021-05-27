const express = require('express')
// const config = require('config')
const router = express.Router()
const auth = require('../../middleware/auth')
const { check, validationResult } = require('express-validator')
// const normalize = require('normalize-url');
// const checkObjectId = require('../../middleware/checkObjectId');

const Profile = require('../../models/Profile')
const User = require('../../models/User')

// Get User's Profile
router.get('/me', auth, async (req, res) => {
  try {
    let profile = await Profile.findOne({ user: req.user.id }).populate(
      'user',
      ['fullName', 'email']
    )

    console.log(profile)
    if (!profile) {
      return res.status(400).json({ msg: 'There is no profile for this user' })
    }

    res.json(profile)
  } catch (err) {
    console.error(err.message)
    res.status(500).send('Server Error')
  }
})

// Create or update user profile
router.post(
  '/',
  auth,
  check('fullName', 'Fullname is required').notEmpty(),
  check('location', 'Location is required').notEmpty(),
  async (req, res) => {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() })
    }

    const profile_att = {
      user: req.user.id,
      ...req.body,
    }

    // res.json(profile_att);
    try {
      let profile = await Profile.findOne({ user: req.user.id })
      console.log(profile)
      if (profile) {
        console.log('prifle updated')
        profile = await Profile.findOneAndUpdate(
          { user: req.user.id },
          { $set: profile_att },
          { new: true, upsert: true, setDefaultsOnInsert: true }
        )
        return res.json(profile)
      }

      profile = new Profile(profile_att)
      await profile.save()
      console.log('prifle created')
      res.json(profile)
    } catch (error) {
      console.error(error.message)
      return res.status(500).send('Server Error')
    }
  }
)

// Get all profiles
router.get('/', async (req, res) => {
  try {
    let profiles = await Profile.find().populate('user', ['userName', 'email'])
    res.json(profiles)
  } catch (error) {
    console.error(error.message)
    return res.status(500).send('Server Error')
  }
})

// Get profile by username
router.get('/:username', async (req, res) => {
  // console.log(req.params.username);
  try {
    let user = await User.findOne({
      userName: req.params.username,
    })

    let profile = await Profile.findOne({
      user: user.id,
    }).populate('user', ['userName', 'email'])
    res.json(profile)
  } catch (error) {
    console.error(error.message)
    return res.status(500).send('Server Error')
  }
})

// Delete User's Profile
router.delete('/', auth, async (req, res) => {
  try {
    await Profile.findOneAndRemove({ user: req.user.id })
    res.json({ msg: `Profile for User ${req.user.userName}` })
    console.log(req.user)
  } catch (error) {
    console.error(error.message)
    return res.status(500).send('Server Error')
  }
})

module.exports = router
