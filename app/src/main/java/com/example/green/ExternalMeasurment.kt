package com.example.green


import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.Visibility
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


class ExternalMeasurment : AppCompatActivity(), OnMapReadyCallback {

    val WEATHER_SAMPLE_SIZE = 10
    var requests_completed = 0
    val request_responces = mutableListOf<DarkSkyApi.Model.wData>()
    var gMap: GoogleMap? = null
    var temp: Int? = null
    var precipration: Int? = null
    var lat: Double? = null
    var lng: Double? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_measurment)
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
            if (lat!=null && lng != null){
                val loc = Location("")
                loc.latitude = lat!!
                loc.longitude = lng!!
                Log.d("LOCATION", loc.toString())
                connectToApi(loc)
            }
        }

        plants.setOnClickListener {
            if (temp != null && precipration != null){
                startActivity<PlantList>(getString(R.string.IntentTemp) to temp, getString(R.string.IntentPrecip) to precipration)
            } else {
                toast("Fetch data first")
            }
        }
    }

    fun fetchLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation.addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful && task.result != null) {
                    val x = task.result!!
                    lat = x.latitude
                    lng = x.longitude
                    moveMap(x.latitude, x.longitude)
                } else {
                    toast("geolocation fail")
                }
            }
        } else {
            toast("No location permission")
        }
    }

    fun connectToApi (location: Location){
        progressBar.progress = 0
        progressBar.visibility = View.VISIBLE

        val time = System.currentTimeMillis()

        requests_completed = 0
        request_responces.clear()

        val step = DateUtils.YEAR_IN_MILLIS/WEATHER_SAMPLE_SIZE

        for (i in 1..WEATHER_SAMPLE_SIZE){
            Log.d("LOOP", i.toString())
            //The api takes seconds instead of milliseconds
            val point = (time - step * i)/1000
            val call = DarkSkyApi.service.dateAndLocation(location.latitude.toString(),
                    location.longitude.toString(), point.toString())
            val result = object: Callback<DarkSkyApi.Model.WeatherData>{
                override fun onResponse(call: Call<DarkSkyApi.Model.WeatherData>, response: Response<DarkSkyApi.Model.WeatherData>) {
                    onRequestCompletion((response.body() as DarkSkyApi.Model.WeatherData).daily.data[0])
                }

                override fun onFailure(call: Call<DarkSkyApi.Model.WeatherData>, t: Throwable) {
                    onRequestCompletion(null)
                    Log.d("Error", t.toString())
                }
            }
            call.enqueue(result)
        }
    }

    fun onRequestCompletion(data: DarkSkyApi.Model.wData?){
        requests_completed++
        progressBar.progress++
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
            precipration = precip.toInt()
            //Fahrenheit
            temp = (tempMin*9/5+32).toInt()
            PrecipValue.text = precip.toString()
            tempValue.text = tempMin.toString()
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0!!
        gMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0,0.0)))
        p0.setOnMapLongClickListener {
            p0.clear()
            gMap!!.addMarker(MarkerOptions().position(it).visible(true))
            lat = it.latitude
            lng = it.longitude
            Log.d("REEE", it.toString())
        }
    }

    fun moveMap(lat:Double, lng:Double){
        if (gMap != null){
            gMap!!.clear()
            val pos = LatLng(lat,lng)
            gMap!!.moveCamera(CameraUpdateFactory.zoomTo(11f))
            gMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat,lng)))
            gMap!!.addMarker(MarkerOptions().position(pos))
        }
    }
}
