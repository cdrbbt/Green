package com.example.green

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.plant_detail.*
import java.lang.Exception

class PlantDetail : Fragment() {
    lateinit var model: PlantListViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.plant_detail, container, false)
    }

    /*in general the app isn't translatable due to the large amount of english text in the db
    so usage of strings is not an issue*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val plant = model.selected!!
        name.text = plant.commonName
        tempValue.text = "${plant.tempMin}F"
        precipValue.text = "${plant.precipMin} to ${plant.precipMax} in/y"
        growthValue.text = plant.growthHabit
        soilPHvalue.text = "${plant.phMin} to ${plant.phMax}"
        rootDepthValue.text = "${plant.rootMin} inches"
        soilCoarseValue.text = plant.coarseSoil
        soilMediumValue.text = plant.mediumSoil
        soilFineValue.text = plant.fineSoil
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        model =  activity?.run { ViewModelProviders.of(this).get(PlantListViewModel::class.java)}
                ?: throw Exception("Invalid Activity")
    }

    override fun onDetach() {
        super.onDetach()
    }
}
