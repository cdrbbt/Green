package com.example.green

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Check if the necessary sensors are present on the device
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

        //Ask for permissions needed for feature
        external.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else{
                val intent = Intent(this,ExternalMeasurement::class.java)
                startActivity(intent)
            }
        }

        library.setOnClickListener {
            startActivity<PlantList>()
        }

        //Check shared preferences if the app DB has been created, if not create from text file in background
        val preferences = this.getSharedPreferences(this.getString(R.string.DBpreference), Context.MODE_PRIVATE)
        if (!preferences.getBoolean(this.getString(R.string.DBinit), false)){
            val generateDB = OneTimeWorkRequestBuilder<DBworker>().build()
            WorkManager.getInstance().enqueue(generateDB)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            val intent = Intent(this,ExternalMeasurement::class.java)
            startActivity(intent)
        }
    }
}
