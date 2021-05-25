const axios = require('axios')
const express = require('express')
const config = require('config')
const router = express.Router()
const Data = require('../../models/Data')

router.get('/', async (req, res) => {
  let token = config.get('WAQI_KEY')
  let link = 'https://api.waqi.info'
  let endpoints = 'search'
  let params = `keyword=Jakarta&token=${token}`
  let realData

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
    let rawData = await axios.get(`${link}/${endpoints}/?${params}`)
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

      // Fetch weather data from OPENWEATHER API
      token = config.get('OPENWEATHER_KEY')
      link = 'http://api.openweathermap.org/data/2.5'
      endpoints = 'weather'
      params = `lat=${data.geo.latitude}&lon=${data.geo.longitude}&appid=${token}`

      let weatherData = await axios.get(`${link}/${endpoints}?${params}`)
      data.weather.humidity = weatherData.data.main.humidity

      // Fetch weather data from WEATHERBIT API
      token = config.get('WEATHERBIT_KEY')
      link = 'http://api.weatherbit.io/v2.0'
      endpoints = 'current'
      params = `lat=${data.geo.latitude}&lon=${data.geo.longitude}&key=${token}`

      weatherData = await axios.get(`${link}/${endpoints}?${params}`)

      data.weather.windDir = weatherData.data.data[0].wind_cdir_full
      data.weather.windSpeed = weatherData.data.data[0].wind_spd
      data.weather.precip = weatherData.data.data[0].precip
      data.weather.temp = weatherData.data.data[0].temp
      data.weather.desc = weatherData.data.data[0].weather.description

      // Fetch air quality data from WEATHERBIT API
      endpoints = 'current/airquality'
      params = `lat=${data.geo.latitude}&lon=${data.geo.longitude}&key=${token}`

      weatherData = await axios.get(`${link}/${endpoints}?${params}`)

      data.airQuality.aqi = weatherData.data.data[0].aqi
      data.airQuality.no = weatherData.data.data[0].no
      data.airQuality.o3 = weatherData.data.data[0].o3
      data.airQuality.so2 = weatherData.data.data[0].no2
      data.airQuality.no2 = weatherData.data.data[0].no2
      data.airQuality.pm10 = weatherData.data.data[0].pm10
      data.airQuality.pm25 = weatherData.data.data[0].pm25

      return data.save()
    })

    await Promise.all(promises)

    let cobaData = await Data.find().select({ _id: 0, __v: 0 })
    //     let cobaData2 = await Data.find()
    console.log(cobaData)
    //     console.log(cobaData2)
    res.json(cobaData)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the WAQI API server")
  }
})

module.exports = router
