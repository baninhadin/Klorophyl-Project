const express = require('express')
const router = express.Router()
const bcrypt = require('bcryptjs')
const jwt = require('jsonwebtoken')
const config = require('config')
const { check, validationResult } = require('express-validator')
// const normalize = require('normalize-url');

const User = require('../../models/User')

// Getting all
router.get('/', async (req, res) => {
  try {
    const users = await User.find()
    res.json(users)
  } catch (err) {
    res.status(500).json({ message: err.message })
  }
})

// Getting One
router.get('/:userName', getUser, (req, res) => {
  res.json(res.user)
})

// Creating one
router.post(
  '/',
  check('userName', 'Username is required').notEmpty(),
  check('email', 'Please include a valid email').isEmail(),
  check(
    'password',
    'Please enter a password with 6 or more characters'
  ).isLength({ min: 6 }),
  async (req, res) => {
    // If one of the validation returns an error
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() })
    }

    const { userName, email, password } = req.body
    try {
      let user = await User.findOne({ userName })

      if (user) {
        return res
          .status(400)
          .json({ errors: [{ msg: 'User already exists' }] })
      }

      user = new User({
        userName,
        email,
        password,
      })

      const salt = await bcrypt.genSalt(10)

      user.password = await bcrypt.hash(password, salt)

      await user.save()

      const payload = {
        user: {
          id: user.id,
        },
      }

      jwt.sign(
        payload,
        config.get('jwtSecret'),
        { expiresIn: '5 days' },
        (err, token) => {
          if (err) throw err
          res.json({ token })
        }
      )
    } catch (err) {
      console.error(err.message)
      res.status(500).send('Server error')
    }
  }
)

// Updating One
router.patch('/:userName', getUser, async (req, res) => {
  if (req.body.userName != null) {
    res.user.userName = req.body.userName
  }
  if (req.body.email != null) {
    res.user.email = req.body.email
  }
  // if (req.body.fullName != null) {
  // res.user.fullName = req.body.fullName;
  // }
  try {
    const updatedUser = await res.user.save()
    res.json(updatedUser)
  } catch (err) {
    res.status(400).json({ message: err.message })
  }
})

// Deleting One
router.delete('/:userName', getUser, async (req, res) => {
  try {
    await res.user.remove()
    res.json({ message: 'Deleted User' })
  } catch (err) {
    res.status(500).json({ message: err.message })
  }
})

// Middleware to get user
async function getUser(req, res, next) {
  let user
  try {
    user = await User.findOne({ userName: req.params.userName })
    if (user == null) {
      return res.status(404).json({ message: 'Cannot find user' })
    }
  } catch (err) {
    return res.status(500).json({ message: err.message })
  }

  res.user = user
  next()
}

module.exports = router
