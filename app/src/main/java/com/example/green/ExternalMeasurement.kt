package com.example.green

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_external_measurment.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExternalMeasurement : AppCompatActivity(), OnMapReadyCallback {

    val WEATHER_SAMPLE_SIZE = 10
    var requests_completed = 0
    val request_responces = mutableListOf<DarkSkyApi.Model.wData>()
    var map: GoogleMap? = null
    var temperature: Int? = null
    var precipration: Int? = null
    var latitude: Double? = null
    var longtitude: Double? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_measurment)

        //Progress bar init
        progressBar.visibility = View.INVISIBLE
        progressBar.max = WEATHER_SAMPLE_SIZE
        progressBar.progress = 0

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchLocation()

        resetLoc.setOnClickListener {
            fetchLocation()
        }

        fetch.setOnClickListener {
            if (latitude!=null && longtitude != null){
                val loc = Location("")
                loc.latitude = latitude!!
                loc.longitude = longtitude!!
                Log.d("LOCATION", loc.toString())
                createWeatherRequests(loc)
            }
        }

        plants.setOnClickListener {
            if (temperature != null && precipration != null){
                startActivity<PlantList>(getString(R.string.IntentTemp) to temperature, getString(R.string.IntentPrecip) to precipration)
            } else {
                toast("Fetch data first")
            }
        }
    }

    //Checks permissions and tries to get last location
    fun fetchLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation.addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful && task.result != null) {
                    val x = task.result!!
                    latitude = x.latitude
                    longtitude = x.longitude
                    moveMap(x.latitude, x.longitude)
                } else {
                    toast("geolocation fail")
                }
            }
        } else {
            toast("No location permission")
        }
    }

    //Enqueues requests to darkskyAPI for weather data for sample size days in a year
    fun createWeatherRequests (location: Location){

        progressBar.progress = 0
        progressBar.visibility = View.VISIBLE

        val time = System.currentTimeMillis()

        requests_completed = 0
        request_responces.clear()

        val step = DateUtils.YEAR_IN_MILLIS/WEATHER_SAMPLE_SIZE

        for (i in 1..WEATHER_SAMPLE_SIZE){
            //The api takes seconds instead of milliseconds
            val timepoint = (time - step * i)/1000

            val call = DarkSkyApi.service.dateAndLocation(location.latitude.toString(),
                    location.longitude.toString(), timepoint.toString())

            val result = object: Callback<DarkSkyApi.Model.WeatherData>{
                override fun onResponse(call: Call<DarkSkyApi.Model.WeatherData>, response: Response<DarkSkyApi.Model.WeatherData>) {
                    onWeatherRequestResult((response.body() as DarkSkyApi.Model.WeatherData).daily.data[0])
                }
                override fun onFailure(call: Call<DarkSkyApi.Model.WeatherData>, t: Throwable) {
                    onWeatherRequestResult(null)
                }
            }
            call.enqueue(result)
        }
    }

    fun onWeatherRequestResult(data: DarkSkyApi.Model.wData?){

        requests_completed++
        progressBar.progress++

        if (data != null) {
            request_responces.add(data)
        }

        //When all requests are done, aggregate the data to a yearly level
        if (requests_completed == WEATHER_SAMPLE_SIZE) {
            var tempMin = Float.MAX_VALUE
            var precip = 0f

            for (response in request_responces) {
                /*The data fetched from the server contains intensity of the rain when it was raining
                in inches per hour, this assumes that it rained for 3 hours, the api also has hourly
                 data however it's not always available and comes with large network load*/
                precip += response.precipIntensity*3
                if (tempMin > response.temperatureMin){
                    tempMin = response.temperatureMin
                }
            }

            //Averaging per year
            precip = (precip/WEATHER_SAMPLE_SIZE) * 365
            precipration = precip.toInt()

            //Converting to fahrenheit, that's what the db uses
            temperature = (tempMin*9/5+32).toInt()
            PrecipValue.text = precip.toString()
            tempValue.text = temperature.toString()
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!
        map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0,0.0)))
        p0.setOnMapLongClickListener {
            p0.clear()
            map!!.addMarker(MarkerOptions().position(it).visible(true))
            latitude = it.latitude
            longtitude = it.longitude
            Log.d("REEE", it.toString())
        }
    }

    fun moveMap(lat:Double, lng:Double){
        if (map != null){
            map!!.clear()
            val pos = LatLng(lat,lng)
            map!!.moveCamera(CameraUpdateFactory.zoomTo(11f))
            map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat,lng)))
            map!!.addMarker(MarkerOptions().position(pos))
        }
    }
}
