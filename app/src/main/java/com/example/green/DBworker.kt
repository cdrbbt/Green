package com.example.green

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedReader
import java.io.InputStreamReader

class DBworker(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val context = applicationContext
        val dbFile = context.resources.openRawResource(R.raw.rawdb)
        BufferedReader(InputStreamReader(dbFile)).lineSequence().iterator().forEach {

            /*Sometimes "a, b, c" occurs, so it's replaced with a b c to get rid of the excessive
            "" and commas*/
            val quoteO = it.indexOf("\"")
            if (quoteO != -1) {
                val quoteC = it.indexOf("\"", quoteO+1)
                val problemString = it.substring(quoteO+1, quoteC)
                val replacementString = problemString.filter {
                    it != ",".first()
                }
                val fixedString = it.replaceRange(quoteO, quoteC+1, replacementString)
                val plantParameters = fixedString.split(",")
                val plant = makeEntry(plantParameters)
                PlantDB.get(context).plantDao().addPlant(plant)
            } else {
                val plant = makeEntry(it.split(","))
                PlantDB.get(context).plantDao().addPlant(plant)
            }
        }
        val preferences = context.getSharedPreferences(context.getString(R.string.DBpreference), Context.MODE_PRIVATE)
        preferences.edit().putBoolean(context.getString(R.string.DBinit), true).apply()
        return Worker.Result.SUCCESS
    }

    
    private fun makeEntry(x:List<String>): PlantModel{
        return PlantModel(x[0],x[1],x[2].capitalize(),x[3],x[4],x[5],x[6],x[7],x[8],x[9],x[10],x[11],x[12],x[13].toInt(),x[14],x[15],
                x[16].toFloat(),x[17].toFloat(),x[18].toInt(),x[19].toInt(),x[20].toInt(),x[21].toInt(),x[22].toInt(),x[23],x[24],x[25].toInt())
    }
}