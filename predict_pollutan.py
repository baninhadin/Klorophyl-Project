import urllib.request
import json
import pandas as pd    
import datetime as DT
import joblib
import math
import tensorflow as tf
import numpy as np
import sys
import operator
import requests
import logging

#Change this
#scaler path
all_scaler_path = "/content/gdrive/MyDrive/scaler/all scaler.gz"
wind_encoder_path = "/content/gdrive/MyDrive/scaler/wind encoder.gz"
co_scaler_path = "/content/gdrive/MyDrive/scaler/co scaler.gz"
pm10_scaler_path = "/content/gdrive/MyDrive/scaler/pm10 scaler.gz"
o3_scaler_path = "/content/gdrive/MyDrive/scaler/o3 scaler.gz"
no2_scaler_path = "/content/gdrive/MyDrive/scaler/no2 scaler.gz"
so2_scaler_path = "/content/gdrive/MyDrive/scaler/so2 scaler.gz"

#model path
co_model_path = "/content/gdrive/MyDrive/Model/co_model.h5"
no2_model_path = "/content/gdrive/MyDrive/Model/no2_model.h5"
o3_model_path = "/content/gdrive/MyDrive/Model/o3_model.h5"
pm10_model_path = "/content/gdrive/MyDrive/Model/pm10_model.h5"
so2_model_path = "/content/gdrive/MyDrive/Model/so2_model.h5"

#load model
pm10_model= tf.keras.models.load_model(pm10_model_path)
so2_model = tf.keras.models.load_model(so2_model_path)
co_model = tf.keras.models.load_model(co_model_path)
o3_model = tf.keras.models.load_model(o3_model_path)
no2_model = tf.keras.models.load_model(no2_model_path)

#load
encoder = joblib.load(wind_encoder_path)
scaler = joblib.load(all_scaler_path)
co_scaler = joblib.load(co_scaler_path)
no2_scaler = joblib.load(no2_scaler_path)
o3_scaler = joblib.load(o3_scaler_path)
pm10_scaler = joblib.load(pm10_scaler_path)
so2_scaler = joblib.load(so2_scaler_path)

def Linear(AQIhigh, AQIlow, Conchigh, Conclow, Concentration):
  return round(((Concentration-Conclow)/(Conchigh-Conclow))*(AQIhigh-AQIlow)+AQIlow)

def AQIPM10(Concentration):
  c= math.floor(Concentration)
  if (c>=0 and c<55):
    AQI=Linear(50,0,54,0,c)

  elif (c>=55 and c<155):
    AQI=Linear(100,51,154,55,c)
  
  elif (c>=155 and c<255):
    AQI=Linear(150,101,254,155,c)

  elif (c>=255 and c<355):
    AQI=Linear(200,151,354,255,c)
  
  elif (c>=355 and c<425):
    AQI=Linear(300,201,424,355,c)
    
  elif (c>=425 and c<505):
    AQI=Linear(400,301,504,425,c)

  elif (c>=505 and c<605):
    AQI=Linear(500,401,604,505,c)

  return AQI

def AQICO(Concentration):
  Conc = (24.45 * (Concentration/1000)) / 28.01
  c=(math.floor(10*Conc))/10
  if (c>=0 and c<4.5):
    AQI=Linear(50,0,4.4,0,c)
  
  elif (c>=4.5 and c<9.5):
    AQI=Linear(100,51,9.4,4.5,c)
    
  elif (c>=9.5 and c<12.5):
    AQI=Linear(150,101,12.4,9.5,c)
    
  elif (c>=12.5 and c<15.5):
    AQI=Linear(200,151,15.4,12.5,c)
  
  elif (c>=15.5 and c<30.5):
    AQI=Linear(300,201,30.4,15.5,c)
    
  elif (c>=30.5 and c<40.5):
    AQI=Linear(400,301,40.4,30.5,c)
    
  elif (c>=40.5 and c<50.5):
    AQI=Linear(500,401,50.4,40.5,c)
  
  return AQI

def AQISO2(Concentration):
  Conc = ((24.45 * (Concentration/1000)) / 64.06) * 1000
  c=math.floor(Conc)
  if (c>=0 and c<36):
  	AQI=Linear(50,0,35,0,c)
  
  elif (c>=36 and c<76):
    AQI=Linear(100,51,75,36,c)

  elif (c>=76 and c<186):
    AQI=Linear(150,101,185,76,c)
  
  elif (c>=186 and c<=304):
    AQI=Linear(200,151,304,186,c)

  elif (c>=304 and c<=604):
    AQI=Linear(300,201,604,305,c)

  elif (c>=605 and c<805):
    AQI=Linear(400,301,804,605,c)

  elif (c>=805 and c<=1004):
	  AQI=Linear(500,401,1004,805,c)

  return AQI

def AQIOzone(Concentration):
  Conc= ((24.45 * (Concentration/1000)) / 48) * 1000
  c=(math.floor(Conc))/1000
  if (c>=0 and c<=.124):
    if (c>=0 and c<.055):
      AQI=Linear(50,0,0.054,0,c)
    
    elif (c>=.055 and c<.071):
      AQI=Linear(100,51,.070,.055,c)

    elif (c>=.071 and c<.086):
      AQI=Linear(150,101,.085,.071,c)

    elif (c>=.086 and c<.106):
      AQI=Linear(200,151,.105,.086,c)
    
    elif (c>=.106 and c<.201):
      AQI=Linear(300,201,.200,.106,c)

  elif (c>=.125 and c<.165):
    AQI=Linear(150,101,.164,.125,c)
  
  elif (c>=.165 and c<.205):
    AQI=Linear(200,151,.204,.165,c)

  elif (c>=.205 and c<.405):
    AQI=Linear(300,201,.404,.205,c)
  
  elif (c>=.405 and c<.505):
    AQI=Linear(400,301,.504,.405,c)
  
  elif (c>=.505 and c<.605):
    AQI=Linear(500,401,.604,.505,c)
  
  return AQI


def AQINO2(Concentration):
  Conc= ((24.45 * (Concentration/1000)) / 46.01) * 1000
  c=(math.floor(Conc))/1000

  if (c>=0 and c<.054):
    AQI=Linear(50,0,.053,0,c)
  
  elif (c>=.054 and c<.101):
    AQI=Linear(100,51,.100,.054,c)

  elif (c>=.101 and c<.361):
    AQI=Linear(150,101,.360,.101,c)

  elif (c>=.361 and c<.650):
    AQI=Linear(200,151,.649,.361,c)

  elif (c>=.650 and c<1.250):
    AQI=Linear(300,201,1.249,.650,c)

  elif (c>=1.250 and c<1.650):
    AQI=Linear(400,301,1.649,1.250,c)

  elif (c>=1.650 and c<=2.049):
    AQI=Linear(500,401,2.049,1.650,c)

  return AQI

def wind_dir_alias(direction):
  if direction == "north" or direction == "north-northeast":
    return "N"
  elif direction == "northeast" or direction == "east-northeast":
    return "NE"
  elif direction == "east" or direction == "east-southeast":
    return "E"
  elif direction == "southeast" or direction == "south-southeast":
    return "SE"
  elif direction == "south-southwest" or direction == "southwest":
    return "SW"
  elif direction == "west-southwest" or direction == "west":
    return "W"
  elif direction == "west-northwest" or direction == "northwest":
    return "NW"
  elif direction == "south" or direction == "south-southeast":
    return "S"
  else:
    return "C"
#date format yyyy-mm-dd
def get_data_api(lokasi):
  today = DT.date.today()
  week_ago = today - DT.timedelta(days=7)
  request= urllib.request.Request(f"https://hidden-will-313103.uc.r.appspot.com/api/data/history?name={lokasi}&start_date={week_ago}&end_date={today}")
  response = urllib.request.urlopen(request)
  data = response.read()
  data = json.loads(data)
  df = pd.json_normalize(data)
  df.dropna(inplace=True)
  df["date"] = df["date"].apply(lambda x: x.split("T")[0])
  df["weather.windDir"] = df["weather.windDir"].apply(lambda x: wind_dir_alias(x))
  num = df.groupby('date')["weather.windDir"].value_counts()
  df = df.groupby('date').mean()
  day = week_ago
  wind_dir_max = []
  for x,y in num.index:
    if str(day) in x:
      wind_dir_max.append(y)
      day = day + DT.timedelta(days=1)
  df["weather.windDir"]  = wind_dir_max

  #df.set_index("date", inplace=True)
  return df[["weather.temp", "weather.humidity", "weather.precip", "weather.windSpeed", "weather.windDir", "airQuality.pm10", "airQuality.so2", "airQuality.co", "airQuality.o3", "airQuality.no2"]]

def convert_AQI(df):
  df["airQuality.pm10"] = df["airQuality.pm10"].apply(lambda x: AQIPM10(x))
  df["airQuality.so2"] = df["airQuality.so2"].apply(lambda x: AQISO2(x))
  df["airQuality.co"] = df["airQuality.co"].apply(lambda x: AQICO(x))
  df["airQuality.o3"] = df["airQuality.o3"].apply(lambda x: AQIOzone(x))
  df["airQuality.no2"] = df["airQuality.no2"].apply(lambda x: AQINO2(x))
  return df

def scale_data(df):
  values = df.values
  values[:,4] = encoder.transform(values[:,4])
  values = values.astype('float32')
  scaled = scaler.transform(values)
  scaled = scaled.reshape(1, 7, 10)
  return scaled

def conc_predict(gas_name, scaled_data):
  if gas_name == "pm10":
    y_pred  = pm10_model.predict(scaled_data)
    inv_ypred = pm10_scaler.inverse_transform(y_pred[:,0].reshape(-1, 1))
    return inv_ypred

  elif gas_name == "so2":
    y_pred = so2_model.predict(scaled_data)
    inv_ypred = so2_scaler.inverse_transform(y_pred)
    return inv_ypred

  elif gas_name == "co":
    y_pred = co_model.predict(scaled_data)
    inv_ypred = co_scaler.inverse_transform(y_pred)
    return inv_ypred

  elif gas_name == "o3":
    y_pred = o3_model.predict(scaled_data)
    inv_ypred = o3_scaler.inverse_transform(y_pred)
    return inv_ypred
  
  elif gas_name == "no2":
    y_pred = no2_model.predict(scaled_data)
    inv_ypred = no2_scaler.inverse_transform(y_pred)
    return inv_ypred

def get_result(location):
  df = get_data_api(location)
  df = convert_AQI(df)
  gas = ["pm10", "co", "no2", "o3", "so2"]
  challenge = {"Location": location,"challenge_co": 50, "challenge_pm10":50, "challenge_o3": 50, "challenge_so2": 50, "challenge_no2":50}
  predicted = {}
  scaled = scale_data(df)
  for x in gas:
    predicted[x] = float(conc_predict(x, scaled))
  sorted_data = sorted(predicted.items(), key = lambda kv:(kv[1], kv[0]))
  for gas_name, koef in zip(sorted_data, range(5)):
    challenge_name = f"challenge_{gas_name[0]}"
    challenge[challenge_name] = 50 + koef*25
  data = json.dumps(challenge)
  url = "https://hidden-will-313103.uc.r.appspot.com/api/challenge"
  headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
  r = requests.post(url, data, headers=headers)
  return [r.json(), r.status_code]


if __name__ == "__main__":
  result = get_result(sys.argv[1])
  print(result)
  if result[1] != 200:
    logging.exception(result) 