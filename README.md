# Klorophyl-Project 🍃

## Objective
The objective of this project is to build a deep learning model that can predict gas pollutants with the highest Air Quality Index by using multiple previous timesteps. 

## Dataset
The dataset we used in this project is provided by
- [Jakarta Open Data](https://data.jakarta.go.id/)
- [Meteorological, Climatological, and Geophysical Agency: BMKG](https://dataonline.bmkg.go.id/home)

## Features
- Temperature	(°C)
- Humidity (%)
- Precipitation	(mm)
- Wind Speed (m/s)
- Wind Direction (°)
- pm10 (AQI)
- so2	(AQI)
- co	 (AQI)
- o3	 (AQI)
- no2  (AQI)

## Used Libraries
- Tensorflow
- Pandas
- Numpy
- Matplotlib
- Seaborn
- Joblib
- Requests
- Sklearn

## Notebooks
- [```01. Fetch_Air_Quality_Data.ipynb```](https://github.com/baninhadin/Klorophyl-Project/blob/ML/01.%20Fetch_Air_Quality_Data.ipynb) - Fetch air quality data from jakarta open data
- [```02. Processing_Air_Quality_and_Weather_Data.ipynb```](https://github.com/baninhadin/Klorophyl-Project/blob/ML/02.%20Processing_Air_Quality_and_Weather_Data.ipynb) - Perform data cleaning and missing value imputation to the dataset.
- [```03. Visualizing_Air_Quality_Data.ipynb```](https://github.com/baninhadin/Klorophyl-Project/blob/ML/03.%20Visualizing_Air_Quality_Data.ipynb) - Visualizing the dataset.
- [```04. Modeling.ipynb```](https://github.com/baninhadin/Klorophyl-Project/blob/ML/04.%20Modeling.ipynb) - Build model using RNN (LSTM and GRU).
- [```05. Evaluating.ipynb```](https://github.com/baninhadin/Klorophyl-Project/blob/ML/05.%20Evaluating.ipynb) - Evaluate model.
