package com.example.green


import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_external_measurment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExternalMeasurment : AppCompatActivity() {

    val WEATHER_SAMPLE_SIZE = 10
    var requests_completed = 0
    val request_responces = mutableListOf<DarkSkyApi.Model.wData>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_measurment)
        checkPermissions()
    }

    fun checkPermissions(){
        val time = System.currentTimeMillis()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation.addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful && task.result != null) {
                    Log.d("GEOLOCATION", "${task.result.latitude},${task.result.longitude},$time")
                    getWeatherData(task.result, time, WEATHER_SAMPLE_SIZE)
                } else {
                    Log.d("GEOLOCATION", "TASKFAIL")
                    Log.d("GEOLOCATION", task.exception.toString())
                }
            }
        } else {
            Log.d("GEOLOCATION", "FAIL")
        }
    }

    fun getWeatherData (location: Location, time: Long, samples: Int){

        requests_completed = 0
        request_responces.clear()

        val step = DateUtils.YEAR_IN_MILLIS/samples

        for (i in 1..samples){
            //The api takes seconds instead of milliseconds
            val point = (time - step * i)/1000
            val call = DarkSkyApi.service.dateAndLocation(location.latitude.toString(),
                    location.longitude.toString(), point.toString())
            val result = object: Callback<DarkSkyApi.Model.WeatherData>{
                override fun onResponse(call: Call<DarkSkyApi.Model.WeatherData>, response: Response<DarkSkyApi.Model.WeatherData>) {
                    val data = (response.body() as DarkSkyApi.Model.WeatherData).daily.data[0]
                    Log.d("WEATHER API", i.toString())
                    Log.d("WEATHER API", (response.body().toString()))
                    Log.d("WEATHER API", ((response.body() as DarkSkyApi.Model.WeatherData).daily.data[0].temperatureMin.toString()))
                    onRequestCompletion((response.body() as DarkSkyApi.Model.WeatherData).daily.data[0])
                }

                override fun onFailure(call: Call<DarkSkyApi.Model.WeatherData>, t: Throwable) {
                    Log.d("WEATHER API", "FAIL ${t.toString()}")
                    onRequestCompletion(null)
                }
            }
            call.enqueue(result)
        }
    }

    fun onRequestCompletion(data: DarkSkyApi.Model.wData?){
        requests_completed++
        if (data != null) {
            request_responces.add(data)
        }

        if (requests_completed == WEATHER_SAMPLE_SIZE) {
            var tempMin = Float.MAX_VALUE
            var precip = 0f
            for (response in request_responces) {
                precip += response.precipIntensity*3
                if (tempMin > response.temperatureMin){
                    tempMin = response.temperatureMin
                }
            }
            precip = (precip/WEATHER_SAMPLE_SIZE) * 365
            PrecipValue.text = precip.toString()
            tempValue.text = tempMin.toString()
            Log.d("RESULTS", "$tempMin, and $precip")
        }
    }
}
