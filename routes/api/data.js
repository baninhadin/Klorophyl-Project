const axios = require('axios')
const express = require('express')
const config = require('config')
const router = express.Router()
const Data = require('../../models/Data')
var cron = require('node-cron')


var degToCard = function(deg){
  if (deg>11.25 && deg<=33.75){
    return "north-northeast";
  }else if (deg>33.75 && deg<=56.25){
    return "east-northeast";
  }else if (deg>56.25 && deg<=78.75){
    return "east";
  }else if (deg>78.75 && deg<=101.25){
    return "east-southeast";
  }else if (deg>101.25 && deg<=123.75){
    return "east-southeast";
  }else if (deg>123.75 && deg<=146.25){
    return "southeast";
  }else if (deg>146.25 && deg<=168.75){
    return "south-southeast";
  }else if (deg>168.75 && deg<=191.25){
    return "south";
  }else if (deg>191.25 && deg<=213.75){
    return "south-southwest";
  }else if (deg>213.75 && deg<=236.25){
    return "southwest";
  }else if (deg>236.25 && deg<=258.75){
    return "west-southwest";
  }else if (deg>258.75 && deg<=281.25){
    return "west";
  }else if (deg>281.25 && deg<=303.75){
    return "west-northwest";
  }else if (deg>303.75 && deg<=326.25){
    return "northwest";
  }else if (deg>326.25 && deg<=348.75){
    return "north-northwest";
  }else{
    return "north"; 
  }
}

cron.schedule('0 */3 * * *', async () => {
// let fetchData = async () => {
  console.log(
    'Fetching data and storing it to database every 3 hour'
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
      endDate = endDate.toISOString().slice(0, 13).replace('T', ':')

    realData = rawData.data.data

    // Looping through the location and fetching data on other api based on fetched data
    const promises = await realData.map(async (datas) => {
      // Fetch weather data from WEATHERBIT API
      token = config.get('WEATHERBIT_KEY_2')
      link = 'http://api.weatherbit.io/v2.0'
      endpoints = 'history/hourly'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}&start_date=${startDate}&end_date=${endDate}`
      console.log(`${link}/${endpoints}?${params}`)

      let weatherData = await axios.get(`${link}/${endpoints}?${params}`)
      // console.log(weatherData.data.data)
       endDate = new Date()
       startDate = new Date(endDate.getTime() - 2 * 24 * 60 * 60 * 1000)
        .toISOString()
        .slice(0, 13)
        .replace('T', ':')
        endDate = new Date(endDate.getTime() - 1 * 24 * 60 * 60 * 1000)
        .toISOString()
        .slice(0, 13)
        .replace('T', ':')
        params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}&start_date=${startDate}&end_date=${endDate}`

      let weatherData1 = await axios.get(`${link}/${endpoints}?${params}`)
      endDate = new Date()
      startDate = new Date(endDate.getTime() - 3 * 24 * 60 * 60 * 1000)
       .toISOString()
       .slice(0, 13)
       .replace('T', ':')
       endDate = new Date(endDate.getTime() - 2 * 24 * 60 * 60 * 1000)
       .toISOString()
       .slice(0, 13)
       .replace('T', ':')    
       params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}&start_date=${startDate}&end_date=${endDate}`   
      let weatherData2 = await axios.get(`${link}/${endpoints}?${params}`)

      let totalWeatherData = weatherData.data.data.concat(weatherData1.data.data).concat(weatherData2.data.data)
 

      // Fetch air quality data from WEATHERBIT API
      endpoints = 'history/airquality'
      params = `lat=${datas.station.geo[0]}&lon=${datas.station.geo[1]}&key=${token}`

      let aqiData = await axios.get(`${link}/${endpoints}?${params}`)

      const aqiPromises = await aqiData.data.data.map(async (item) => {
        let data = new Data({
          name: datas.station.name,
          geo: {
            latitude: datas.station.geo[0],
            longitude: datas.station.geo[1],
          },
        })


        let foundWeatherData = totalWeatherData.find((items) => {
          return items.timestamp_utc == item.timestamp_utc
        })

        data.date = item.timestamp_utc
        data.airQuality.aqi = item.aqi
        data.airQuality.co = item.co
        data.airQuality.o3 = item.o3
        data.airQuality.so2 = item.so2
        data.airQuality.no2 = item.no2
        data.airQuality.pm10 = item.pm10
        data.airQuality.pm25 = item.pm25

        if (foundWeatherData) {

          data.weather.windDir = degToCard(foundWeatherData.wind_dir)
          data.weather.windSpeed = foundWeatherData.wind_spd
          data.weather.precip = foundWeatherData.precip
          data.weather.temp = foundWeatherData.temp
          data.weather.humidity = foundWeatherData.rh
          data.weather.desc = foundWeatherData.weather.description
        }

        return data.save()
      })


      return aqiPromises
    })

    await Promise.all(promises)

  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
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

    Promise.all(promises).then((data) => res.json(data))

  } catch (err) {
    console.error(err.message)
    res.status(500).send("There's an error on the server")
  }
})
module.exports = router
