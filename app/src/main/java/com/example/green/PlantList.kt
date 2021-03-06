package com.example.green

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
class PlantList : AppCompatActivity(), PlantFragment.OnListFragmentInteractionListener {

    lateinit var model: PlantListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_list)

        model = ViewModelProviders.of(this).get(PlantListViewModel::class.java)

        val precip = intent.getIntExtra(getString(R.string.IntentPrecip),Int.MAX_VALUE)
        val temp = intent.getIntExtra(getString(R.string.IntentTemp), Int.MAX_VALUE)

        val dbDAO = PlantDB.get(this).plantDao()

        when{
            precip != Int.MAX_VALUE -> {
                doAsync {
                    val plants = dbDAO.getTempPrecip(temp,precip)
                    uiThread {
                        updatePlantList(plants)
                    }
                }
            }

            temp != Int.MAX_VALUE ->{
                doAsync {
                    val plants = dbDAO.getTemp(temp)
                    uiThread {
                        updatePlantList(plants)
                    }
                }
            }

            else -> {
                doAsync {
                    val plants = dbDAO.getAll()
                    uiThread {
                        updatePlantList(plants)
                    }
                }
            }
        }
    }

    override fun onListFragmentInteraction(item:PlantModel?) {
        model.selected = item
        val detail = PlantDetail()
        supportFragmentManager.beginTransaction().replace(R.id.ListContainer, detail).addToBackStack(null).commit()

    }

    private fun updatePlantList(plants: LiveData<List<PlantModel>>){
        plants.observe(this, Observer {
            model.plants = it
            if (it.isNotEmpty()) model.selected = it.first()
            else toast(R.string.noPlants)

            val listFragment = PlantFragment()
            supportFragmentManager.beginTransaction().add(R.id.ListContainer, listFragment).commit()
        })
    }
}
