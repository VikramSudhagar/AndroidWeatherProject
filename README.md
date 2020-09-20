# AndroidWeatherProject
It's a learning experience to display the current weather for the device's location or by the city entered by the end user. Later extended to display the weather forecast for seven days as well.

## Implementation Details
* Asked permission to access the device location and used geocoder for transforming city into Latitude/Longitude and viceversa.
* Designed a simple UI for displaying current weather and Recyclerview for the forecast.
* Used openweathermap api to fetch the weather information for the given location.
* Parsed JSON response into the UI.

## ScreenShot
![PHOTO-2020-09-18-13-52-17](https://user-images.githubusercontent.com/69441823/93719354-efe83600-fb36-11ea-998b-3963406ef567.jpg)



### TODO:
* Randomly gets "grpc failed" error, related issue in geocoder? [https://issuetracker.google.com/issues/168043749]
* Happy path works, other conditions need to be fixed.
