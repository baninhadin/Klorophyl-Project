const axios = require('axios')
const express = require('express')
const config = require('config')
const router = express.Router()
const Data = require('../../models/Data')

router.get('/', async (req, res) => {
  try {
    let data = await Data.exists()

    // Check if data exist already
    if (data) {
      console.log('ada')
      let sendData
      if (req.query.search) {
        console.log('/.*' + req.query.search + '.*/i')
        let regex = new RegExp('.*' + req.query.search + '.*', 'i')
        sendData = await Data.findOne({
          name: regex,
        }).select({
          _id: 0,
          __v: 0,
        })
        return res.json(sendData)
      }
      sendData = await Data.find().select({
        _id: 0,
        __v: 0,
      })
      return res.json(sendData)
    }
  } catch (err) {
    console.error(err.message)
    return res.status(500).send('Server Error')
  }

  try {
    let token = config.get('WAQI_KEY')
    let link = 'https://api.waqi.info'
    let endpoints = 'search'
    let params = `keyword=Jakarta&token=${token}`
    let realData
    let rawData = await axios.get(`${link}/${endpoints}/?${params}`)
    //     let endDate = new Date()
    //     let startDate = new Date(endDate.getTime() - 1 * 24 * 60 * 60 * 1000)
    let startDate = '2021-05-23'
    let endDate = '2021-05-24'
    realData = rawData.data.data

    const promises = realData.map(async (datas) => {
      // Create new data
      let data = new Data({
        name: datas.station.name,
        geo: {
          latitude: datas.station.geo[0],
          longitude: datas.station.geo[1],
        },
        date: datas.time.stime,
      })

      // Fetch weather data from WEATHERBIT API
      token = config.get('WEATHERBIT_KEY')
      link = 'http://api.weatherbit.io/v2.0'
      endpoints = 'history/hourly'
      params = `lat=${data.geo.latitude}&lon=${data.geo.longitude}&key=${token}&start_date=${startDate}&end_date=${endDate}`

      weatherData = await axios.get(`${link}/${endpoints}?${params}`)

      console.log('---------------------------------------------------------')
      const weatherPromises = weatherData.data.data.map(async (item) => {
        //    console.log(item.timestamp_utc)
        let adaData = await Data.findOne({ date: item.timestamp_utc })
        console.log(adaData)
        if (!adaData) {
          console.log('Data baru')
          data.weather.windDir = item.wind_cdir_full
          data.weather.windSpeed = item.wind_spd
          data.weather.precip = item.precip
          data.weather.temp = item.temp
          data.weather.humidity = item.rh
          data.weather.desc = item.weather.description
          return data.save()
        }
      })

      await Promise.all(weatherPromises)

      // Fetch air quality data from WEATHERBIT API
      //  endpoints = 'history/airquality'
      //  params = `lat=${data.geo.latitude}&lon=${data.geo.longitude}&key=${token}&start_date=${startDate}&end_date=${endDate}`

      //  weatherData = await axios.get(`${link}/${endpoints}?${params}`)

      //  data.airQuality.aqi = weatherData.data.data[0].aqi
      //  data.airQuality.no = weatherData.data.data[0].no
      //  data.airQuality.o3 = weatherData.data.data[0].o3
      //  data.airQuality.so2 = weatherData.data.data[0].no2
      //  data.airQuality.no2 = weatherData.data.data[0].no2
      //  data.airQuality.pm10 = weatherData.data.data[0].pm10
      //  data.airQuality.pm25 = weatherData.data.data[0].pm25

      //  return data.save()
    })

    let cobaData = await Data.find().select({ _id: 0, __v: 0 })
    res.json(cobaData)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the WAQI API server")
  }
})

module.exports = router
