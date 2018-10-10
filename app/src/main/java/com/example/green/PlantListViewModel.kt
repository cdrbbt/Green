package com.example.green

import androidx.lifecycle.ViewModel

class PlantListViewModel:ViewModel() {
    var selected: PlantModel? = null
    var plants: List<PlantModel>? = null
}