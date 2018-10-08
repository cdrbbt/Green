package com.example.green

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader


class DBGenerator {

    //Check shared preferences if DB was initialized
    fun checkIfInitialized(context: Context): Boolean{
        val pref = context.getSharedPreferences(context.getString(R.string.DBpreference), Context.MODE_PRIVATE)
        return (pref.getBoolean(context.getString(R.string.DBinit),false))
    }

    fun initializeDB(context: Context){
        if (!checkIfInitialized(context)) {
            val s = context.resources.openRawResource(R.raw.rawdb)
            val r = BufferedReader(InputStreamReader(s)).lineSequence().iterator().forEach {
                val quoteO = it.indexOf("\"")
                if (quoteO != -1) {
                    val quoteC = it.indexOf("\"", quoteO+1)
                    val tr = it.substring(quoteO+1, quoteC)
                    val rep = tr.filter {
                        it != ",".first()
                    }
                    val new = it.replaceRange(quoteO, quoteC+1, rep)
                    val x = new.split(",")
                    val pl = makeEntry(x)
                    PlantDB.get(context).plantDao().addPlant(pl)
                    return
                }
                val pl2 = makeEntry(it.split(","))
                PlantDB.get(context).plantDao().addPlant(pl2)
            }
            val pref = context.getSharedPreferences(context.getString(R.string.DBpreference), Context.MODE_PRIVATE)
            pref.edit().putBoolean(context.getString(R.string.DBinit), true).apply()
        }
    }

    fun makeEntry(x:List<String>): PlantModel{
        return PlantModel(x[0],x[1],x[2].capitalize(),x[3],x[4],x[5],x[6],x[7],x[8],x[9],x[10],x[11],x[12],x[13].toInt(),x[14],x[15],
                x[16].toFloat(),x[17].toFloat(),x[18].toInt(),x[19].toInt(),x[20].toInt(),x[21].toInt(),x[22].toInt(),x[23],x[24],x[25].toInt())
    }

}