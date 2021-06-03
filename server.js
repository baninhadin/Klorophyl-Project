const express = require('express')
const connectDB = require('./config/database')
const bodyParser = require('body-parser')
const app = express()

// Connect Database
connectDB()

// Init Middleware
app.use(express.json())

app.get('/', (req, res) => {
  res.send('Welcome to Klorophyl API!')
})

// Define Routes
app.use('/uploads', express.static('uploads'))
app.use('/api/users', require('./routes/api/users'))
app.use('/api/auth', require('./routes/api/auth'))
app.use('/api/profile', require('./routes/api/profile'))
app.use('/api/data', require('./routes/api/data.js'))
app.use('/api/challenge', require('./routes/api/challenge'))

const PORT = process.env.PORT || 5000

app.listen(PORT, () => console.log(`Server started on port ${PORT}`))
