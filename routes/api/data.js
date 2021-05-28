const axios = require('axios')
const express = require('express')
const config = require('config')
const router = express.Router()
const Data = require('../../models/Data')
var cron = require('node-cron')

// cron.schedule('59 * * * *', async () => {
let fetchData = async () => {
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
    // endDate = new Date(endDate.getTime() - 1 * 24 * 60 * 60 * 1000)
    // .toISOString()
    // .slice(0, 13)
    // .replace('T', ':')
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
      // const weatherPromises = await weatherData.data.data.map(async (item) => {
      //   let data = new Data({
      //     name: datas.station.name,
      //     geo: {
      //       latitude: datas.station.geo[0],
      //       longitude: datas.station.geo[1],
      //     },
      //   })
      //   let adaData = await Data.findOne({ date: item.timestamp_utc })

      //   // If it's a new data
      //   if (!adaData) {
      //     console.log(
      //       `fetch weather. tidak ada data pada timestamp ${item.timestamp_utc}`
      //     )
      //     data.date = item.timestamp_utc
      //     data.weather.windDir = item.wind_cdir_full
      //     data.weather.windSpeed = item.wind_spd
      //     data.weather.precip = item.precip
      //     data.weather.temp = item.temp
      //     data.weather.humidity = item.rh
      //     data.weather.desc = item.weather.description
      //     return data.save()
      //   }

      //   // If data already exist but no weather data
      //   if (!adaData.weather.temp) {
      //     console.log(
      //       `fetch weather. ada data , tidak ada weather pada timestamp ${item.timestamp_utc}`
      //     )
      //     adaData.weather.windDir = item.wind_cdir_full
      //     adaData.weather.windSpeed = item.wind_spd
      //     adaData.weather.precip = item.precip
      //     adaData.weather.temp = item.temp
      //     adaData.weather.humidity = item.rh
      //     adaData.weather.desc = item.weather.description
      //     return adaData.save()
      //   }
      // })

      // Fetch air quality data from WEATHERBIT API
      endpoints = 'history/airquality'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}`

      let aqiData = await axios.get(`${link}/${endpoints}?${params}`)

      // Loop through data from last 72 hours
      // let ketemu = weatherData.data.data.findOne({
      // date: new Date('2021-05-28T03:00:00.000Z'),
      // })

      // console.log(
      // `ada data ${foundWeatherData} weather pada timestamp: ${item.timestamp_utc}`
      // )
      const aqiPromises = await aqiData.data.data.map(async (item) => {
        let data = new Data({
          name: datas.station.name,
          geo: {
            latitude: datas.station.geo[0],
            longitude: datas.station.geo[1],
          },
        })

        let adaData = await Data.findOne({
          date: new Date(item.timestamp_utc),
        })

        let foundWeatherData = weatherData.data.data.find(
          (items) => items.timestamp_utc == item.timestamp_utc
        )
        // if it's a new data
        // if (!adaData) {
        // console.log(
        // `fetch aqi. tidak ada data pada timestamp ${item.timestamp_utc}`
        // )
        data.date = item.timestamp_utc
        data.airQuality.aqi = item.aqi
        data.airQuality.co = item.co
        data.airQuality.o3 = item.o3
        data.airQuality.so2 = item.so2
        data.airQuality.no2 = item.no2
        data.airQuality.pm10 = item.pm10
        data.airQuality.pm25 = item.pm25
        if (foundWeatherData) {
          data.weather.windDir = foundWeatherData.wind_cdir_full
          data.weather.windSpeed = foundWeatherData.wind_spd
          data.weather.precip = foundWeatherData.precip
          data.weather.temp = foundWeatherData.temp
          data.weather.humidity = foundWeatherData.rh
          data.weather.desc = foundWeatherData.weather.description
        }

        // console.log(data)
        // console.log('data tidak ada')
        return data.save()
        // }

        // console.log(
        // `fetch aqi. data sudah lengkap. pada timestamp ${item.timestamp_utc} dengan aqi ${adaData.airQuality.aqi}`
        // )
      })

      // let awaitedAqi = await Promise.all(aqiPromises)
      // let awaitedWeather = await Promise.all(weatherPromises)

      // return awaitedAqi.concat(awaitedWeather)
      return aqiPromises
    })

    await Promise.all(promises)

    // console.log(promises)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
  }
}

fetchData()

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
        .sort({ date: req.query.asc || -1 })
      console.log(queryData.length)
      return res.json(queryData)
    }

    // jika tidak ada query waktu
    let queryData = await Data.find({
      name: nameQuery,
    })
      .select({ _id: 0, __v: 0 })
      .sort({ date: req.query.asc || -1 })
    console.log(queryData.length)
    return res.json(queryData)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
  }
})

router.get('/current', async (req, res) => {
  try {
    let token = config.get('WAQI_KEY')
    let link = 'https://api.waqi.info'
    let endpoints = 'search'
    let params = `keyword=Jakarta&token=${token}`
    let realData

    // Fetching data from WAQI API for the location and name
    let rawData = await axios.get(`${link}/${endpoints}/?${params}`)

    realData = rawData.data.data

    // console.log(realData)

    const promises = await realData.map(async (datas) => {
      // Fetch weather data from WEATHERBIT API
      token = config.get('WEATHERBIT_KEY')
      link = 'http://api.weatherbit.io/v2.0'
      endpoints = 'current'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}`

      let rawWeatherData = await axios.get(`${link}/${endpoints}?${params}`)
      let weatherData = rawWeatherData.data.data

      console.log('------')
      // console.log(weatherData)
      // console.log(weatherData[0])

      let data = {
        name: datas.station.name,
        geo: {
          latitude: datas.station.geo[0],
          longitude: datas.station.geo[1],
        },
        date: new Date(weatherData[0].ob_time),
        weather: {
          windDir: weatherData[0].wind_cdir_full,
          windSpeed: weatherData[0].wind_spd,
          precip: weatherData[0].precip,
          temp: weatherData[0].temp,
          humidity: weatherData[0].rh,
          desc: weatherData[0].weather.description,
        },
        airQuality: {
          aqi: null,
          co: null,
          o3: null,
          so2: null,
          no2: null,
          pm10: null,
          pm25: null,
        },
      }

      endpoints = 'current/airquality'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}`

      let rawAqiData = await axios.get(`${link}/${endpoints}?${params}`)
      let aqiData = rawAqiData.data.data

      // console.log(aqiData[0])
      data.airQuality.aqi = aqiData[0].aqi
      data.airQuality.co = aqiData[0].co
      data.airQuality.o3 = aqiData[0].o3
      data.airQuality.so2 = aqiData[0].so2
      data.airQuality.no2 = aqiData[0].no2
      data.airQuality.pm10 = aqiData[0].pm10
      data.airQuality.pm25 = aqiData[0].pm25

      // console.log(data)
      return data
    })
    // await promises
    //   .map((item) => {
    //     item.then((data) => {
    //       return data
    //     })
    //   })
    //   .then((data) => {
    //     console.log(data)
    //   })
    // console.log(promises)

    // promises.then((data) => console.log(data))
    // await Promise.all(await promises)
    Promise.all(promises).then((data) => res.json(data))
    // res.json(promises)
    // console.log(promises)
    // console.log(sendData)
  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
  }
})
module.exports = router
