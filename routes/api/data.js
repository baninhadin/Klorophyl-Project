const axios = require('axios')
const express = require('express')
const config = require('config')
const router = express.Router()
const Data = require('../../models/Data')
var cron = require('node-cron')

cron.schedule('59 * * * *', async () => {
  // let fetchData = async () => {
  console.log(
    'Fetching data and storing it to database every hour at minute 59'
  )
  try {
    // Initialize variable for fetching data
    let token = config.get('WAQI_KEY')
    let link = 'https://api.waqi.info'
    let endpoints = 'search'
    let params = `keyword=Jakarta&token=${token}`
    let realData

    // Fetching data from WAQI API for the location and name
    let rawData = await axios.get(`${link}/${endpoints}/?${params}`)

    let endDate = new Date()
    let startDate = new Date(endDate.getTime() - 1 * 24 * 60 * 60 * 1000)
      .toISOString()
      .slice(0, 13)
      .replace('T', ':')
    // let startDate = '2021-05-24'
    // let endDate = '2021-05-25'
    endDate = endDate.toISOString().slice(0, 13).replace('T', ':')

    realData = rawData.data.data

    // Looping through the location and fetching data on other api based on fetched data
    const promises = await realData.map(async (datas) => {
      // Fetch weather data from WEATHERBIT API
      token = config.get('WEATHERBIT_KEY')
      link = 'http://api.weatherbit.io/v2.0'
      endpoints = 'history/hourly'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}&start_date=${startDate}&end_date=${endDate}`
      console.log(`${link}/${endpoints}?${params}`)
      let weatherData = await axios.get(`${link}/${endpoints}?${params}`)

      // Loop through data from 1 day span
      const weatherPromises = await weatherData.data.data.map(async (item) => {
        let data = new Data({
          name: datas.station.name,
          geo: {
            latitude: datas.station.geo[0],
            longitude: datas.station.geo[1],
          },
        })
        let adaData = await Data.findOne({ date: item.timestamp_utc })
        console.log(item.timestamp_utc)
        // console.log(adaData)
        // If it's a new data
        if (!adaData) {
          data.date = item.timestamp_utc
          data.weather.windDir = item.wind_cdir_full
          data.weather.windSpeed = item.wind_spd
          data.weather.precip = item.precip
          data.weather.temp = item.temp
          data.weather.humidity = item.rh
          data.weather.desc = item.weather.description
          return data.save()
        }

        // If data already exist but no weather data
        if (!adaData.weather.temp) {
          adaData.weather.windDir = item.wind_cdir_full
          adaData.weather.windSpeed = item.wind_spd
          adaData.weather.precip = item.precip
          adaData.weather.temp = item.temp
          adaData.weather.humidity = item.rh
          adaData.weather.desc = item.weather.description
          return adaData.save()
        }
      })

      // Fetch air quality data from WEATHERBIT API
      endpoints = 'history/airquality'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}`

      let aqiData = await axios.get(`${link}/${endpoints}?${params}`)

      // Loop through data from last 72 hours
      const aqiPromises = await aqiData.data.data.map(async (item) => {
        let data = new Data({
          name: datas.station.name,
          geo: {
            latitude: datas.station.geo[0],
            longitude: datas.station.geo[1],
          },
        })

        let adaData = await Data.findOne({ date: item.timestamp_utc })

        // if it's a new data
        if (!adaData) {
          data.date = item.timestamp_utc
          data.airQuality.aqi = item.aqi
          data.airQuality.no = item.no
          data.airQuality.o3 = item.o3
          data.airQuality.so2 = item.no2
          data.airQuality.no2 = item.no2
          data.airQuality.pm10 = item.pm10
          data.airQuality.pm25 = item.pm25
          console.log('data tidak ada')
          return data.save()
        }

        //if data already exist but no airQuality data
        if (!adaData.airQuality.aqi) {
          console.log('data ada tetapi airQuality tidak ada')
          adaData.airQuality.aqi = item.aqi
          adaData.airQuality.no = item.no
          adaData.airQuality.o3 = item.o3
          adaData.airQuality.so2 = item.no2
          adaData.airQuality.no2 = item.no2
          adaData.airQuality.pm10 = item.pm10
          adaData.airQuality.pm25 = item.pm25
          return adaData.save()
        }
      })

      return aqiPromises.concat(weatherPromises)
      // console.log(weatherPromises)
      // return weatherPromises
    })

    await Promise.all(promises)
  } catch (err) {
    console.error(err.message)
    // res.status(500).send("There's an error on the server")
  }
})

// fetchData()

router.get('/history', async (req, res) => {
  try {
    let nameQuery = new RegExp('.*' + (req.query.name || '') + '.*', 'gi')

    if (req.query.start_date && !req.query.end_date) {
      return res.status(500).send('end_date query must be filled')
    }

    if (!req.query.start_date && req.query.end_date) {
      return res.status(500).send('start_date query must be filled')
    }

    if (req.query.start_date && req.query.end_date) {
      // jika ada query waktu
      let queryData = await Data.find({
        name: nameQuery,
        date: {
          $gte: new Date(req.query.start_date),
          $lt: new Date(req.query.end_date),
        },
      })
        .select({ _id: 0, __v: 0 })
        .sort({ date: 1 })
      console.log(queryData.length)
      return res.json(queryData)
    }

    // jika tidak ada query waktu
    let queryData = await Data.find({
      name: nameQuery,
    })
      .select({ _id: 0, __v: 0 })
      .sort({ date: 1 })
    console.log(queryData.length)
    return res.json(queryData)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
  }
})

module.exports = router
