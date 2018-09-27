package com.example.green


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


object DarkSkyApi {
    val URL = "https://api.darksky.net/forecast/${API_KEY.WEATHER_API_KEY}/"

    val retrofit = Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    object Model{
        data class WeatherData(val timezone: String)

    }

    interface Service {
        @GET("{latittude},{longitude},{time}?exclude=currently,minutely,hourly,flags")
    fun dateAndLocaton(@Path("latitude") latitude: String, @Path("longitude") longitude: String,
                       @Path("time") time: String): Call<Model.WeatherData>
    }

    val service = retrofit.create(Service::class.java)!!
}