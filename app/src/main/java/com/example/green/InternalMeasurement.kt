package com.example.green

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_internal_measurement.*
import kotlin.math.roundToInt

class InternalMeasurement : AppCompatActivity(), SensorEventListener {
    lateinit var sensorManager: SensorManager
    lateinit var humiditySensor: Sensor
    lateinit var temperatureSensor: Sensor
    lateinit var lightSensor: Sensor

    val TEMPERATURE_EXTREME_LOW = 5
    val TEMPERATURE_LOW = 15
    val TEMPERATURE_HIGH =25
    val TEMPERATURE_EXTREME_HIGH = 30

    val HUMIDITY_LOW = 30
    val HUMIDITY_HIGH = 60

    val LIGHT_LOW = 2500
    val LIGHT_MEDIUM = 10000
    val LIGHT_HIGH = 20000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_measurement)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        measure.setOnClickListener{
            startMeasurement()
        }
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    fun startMeasurement(){
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
         when(event?.sensor) {
             humiditySensor -> {
                 sensorManager.unregisterListener(this, humiditySensor)
                 val hValue = event.values[0]

                 when{
                     hValue < HUMIDITY_LOW -> humidityValue.setTextColor(getColor(R.color.Extreme))
                     hValue < HUMIDITY_HIGH -> humidityValue.setTextColor(getColor(R.color.Moderate))
                     else -> humidityValue.setTextColor(getColor(R.color.Good))
                 }

                 val display = "${hValue}%"
                 humidityValue.text = display
             }
             temperatureSensor -> {
                 sensorManager.unregisterListener(this, temperatureSensor)
                 val tValue = event.values[0]
                 when{
                     tValue < TEMPERATURE_EXTREME_LOW || tValue > TEMPERATURE_EXTREME_HIGH ->
                         tempValue.setTextColor(getColor(R.color.Extreme))
                     tValue < TEMPERATURE_LOW || tValue > TEMPERATURE_HIGH ->
                         tempValue.setTextColor(getColor(R.color.Moderate))
                     else -> tempValue.setTextColor(getColor(R.color.Good))
                 }
                 val display = "${tValue}C"
                 tempValue.text = display
             }
             lightSensor -> {
                 sensorManager.unregisterListener(this, lightSensor)
                 val lValue = event.values[0]
                 when{
                     lValue < LIGHT_LOW -> lightValue.setTextColor(getColor(R.color.Extreme))
                     lValue < LIGHT_MEDIUM -> lightValue.setTextColor(getColor(R.color.Moderate))
                     else -> lightValue.setTextColor(getColor(R.color.Good))
                 }
                 val display = "${event.values[0].roundToInt()} lux"
                 lightValue.text = display
             }
             else -> return
         }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}
