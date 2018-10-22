package com.example.green

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_internal_measurement.*
import org.jetbrains.anko.startActivity
import kotlin.math.roundToInt

class InternalMeasurement : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var humiditySensor: Sensor
    private lateinit var temperatureSensor: Sensor
    private lateinit var lightSensor: Sensor

    var temp: Int? = null

    //thresholds for values
    private val TEMPERATURE_LOW = 15
    private val TEMPERATURE_HIGH =25

    private val HUMIDITY_LOW = 30
    private val HUMIDITY_HIGH = 60

    private val LIGHT_LOW = 2500
    private val LIGHT_HIGH = 10000


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

        findPlants.setOnClickListener {
            startActivity<PlantList>(getString(R.string.IntentTemp) to temp!!)
        }

        startMeasurement()
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun startMeasurement(){
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
                     hValue < HUMIDITY_LOW -> {
                         humidityValue.setTextColor(getColor(R.color.Extreme))
                         humidityDesc.text = getString(R.string.lowHumidityDesc)
                     }
                     hValue < HUMIDITY_HIGH -> {
                         humidityValue.setTextColor(getColor(R.color.Moderate))
                         humidityDesc.text = getString(R.string.medHumidityDesc)
                     }
                     else -> {
                         humidityValue.setTextColor(getColor(R.color.Good))
                         humidityDesc.text = getString(R.string.hiHumidityDesc)
                     }
                 }

                 val display = "$hValue%"
                 humidityValue.text = display
             }
             temperatureSensor -> {
                 sensorManager.unregisterListener(this, temperatureSensor)
                 val tValue = event.values[0]
                 when{
                     tValue < TEMPERATURE_LOW ->{
                         tempValue.setTextColor(getColor(R.color.Extreme))
                         tempDesc.text = getString(R.string.lowTempDesc)
                     }
                     tValue < TEMPERATURE_HIGH  ->{
                         tempValue.setTextColor(getColor(R.color.Good))
                         tempDesc.text = getString(R.string.medTempDesc)
                     }

                     else -> {
                         tempValue.setTextColor(getColor(R.color.Extreme))
                         tempDesc.text = getString(R.string.hiTempDesc)
                     }
                 }

                 //fahrenheit
                 temp = (tValue*9/5+32).toInt()
                 tempValue.text = "$temp F"
             }
             lightSensor -> {
                 sensorManager.unregisterListener(this, lightSensor)
                 val lValue = event.values[0]
                 when{
                     lValue < LIGHT_LOW -> {
                         lightValue.setTextColor(getColor(R.color.Extreme))
                         lightDesc.text = getString(R.string.lowLightDesc)
                     }
                     lValue < LIGHT_HIGH -> {
                         lightValue.setTextColor(getColor(R.color.Moderate))
                         lightDesc.text = getString(R.string.medLightDesc)
                     }
                     else -> {
                         lightValue.setTextColor(getColor(R.color.Good))
                         lightDesc.text = getString(R.string.hiLightDesc)
                     }
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
