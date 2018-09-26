package com.example.green

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_internal_measurement.*

class InternalMeasurement : AppCompatActivity(), SensorEventListener {
    lateinit var sensorManager: SensorManager
    lateinit var humiditySensor: Sensor
    lateinit var temperatureSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_measurement)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
         when(event?.sensor) {
             humiditySensor -> humidityValue.text = event.values[0].toString()
             temperatureSensor -> tempValue.text = event.values[0].toString()
             else -> return
         }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
