// require('dotenv').config();

// const express = require('express');
// const app = express();
// const mongoose = require('mongoose');

// mongoose.connect(process.env.DATABASE_URL, {
//   useNewUrlParser: true,
//   useUnifiedTopology: true,
//   useFindAndModify: true,
//   useCreateIndex: true,
// });
// const db = mongoose.connection;
// db.on('error', (error) => console.error(error));
// db.once('open', () => console.log('Connected to Database'));

// app.use(express.json());

// const usersRouter = require('./routes/users');
// app.use('/users', usersRouter);

// app.listen(3000, () => console.log('Server Started'));

const express = require('express')
const connectDB = require('./config/database')

const app = express()

// Connect Database
connectDB()

// Init Middleware
app.use(express.json())

// Define Routes
app.use('/api/users', require('./routes/api/users'))
app.use('/api/auth', require('./routes/api/auth'))
app.use('/api/profile', require('./routes/api/profile'))
// app.use('/api/posts', require('./routes/api/posts'));

// Serve static assets in production
// if (process.env.NODE_ENV === 'production') {
//   // Set static folder
//   app.use(express.static('client/build'));

//   app.get('*', (req, res) => {
//     res.sendFile(path.resolve(__dirname, 'client', 'build', 'index.html'));
//   });
// }

const PORT = process.env.PORT || 5000

app.listen(PORT, () => console.log(`Server started on port ${PORT}`))
