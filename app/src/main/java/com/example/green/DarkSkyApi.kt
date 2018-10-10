package com.example.green


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


object DarkSkyApi {
    private val URL = "https://api.darksky.net/forecast/${API_KEY.WEATHER_API_KEY}/"

    val retrofit = Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
            .build()!!

    object Model{
        data class WeatherData(val daily: Daily)
        data class Daily(val data: List<wData>)
        data class wData(val time: String, val windSpeed:String, val temperatureMin:Float, val precipAccumulation:String,
                         val precipIntensity: Float)

    }

    interface Service {
        @GET("{latitude},{longitude},{time}?exclude=currently,minutely,hourly,flags&units=si")
    fun dateAndLocation(@Path("latitude") latitude: String, @Path("longitude") longitude: String,
                        @Path("time") time: String): Call<Model.WeatherData>
    }

    val service = retrofit.create(Service::class.java)!!
}