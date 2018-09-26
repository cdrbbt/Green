package com.example.green

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


    }
}
