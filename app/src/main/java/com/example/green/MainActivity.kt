package com.example.green

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TEST", "HELP")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        internal.setOnClickListener{
            if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null
                    || sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) == null ){
                val snackbar = Snackbar.make(mainMenu, "Not available", Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                val internalIntent = Intent(this, InternalMeasurement::class.java)
                startActivity(internalIntent)
            }
        }

        external.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else{
                val intent = Intent(this,ExternalMeasurment::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            val intent = Intent(this,ExternalMeasurment::class.java)
            startActivity(intent)
        }
    }
}
