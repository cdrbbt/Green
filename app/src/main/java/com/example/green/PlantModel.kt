package com.example.green

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Plants")
data class PlantModel(
        @PrimaryKey @ColumnInfo(name = "Accepted Symbol") var acceptedSymbol: String,
        @ColumnInfo(name = "Scientific Name") val scientificName: String,
        @ColumnInfo(name = "Common Name") val commonName: String,
        @ColumnInfo(name = "Growth Habit") val growthHabit: String,
        @ColumnInfo(name = "Adapted to Coarse Textured Soils") val coarseSoil: String,
        @ColumnInfo(name = "Adapted to Medium Textured Soils") val mediumSoil: String,
        @ColumnInfo(name = "Adapted to Fine Textured Soils") val fineSoil: String,
        @ColumnInfo(name = "Anaerobic Tolerance") val anaerobicTolerance: String,
        @ColumnInfo(name = "CaCO3 Tolerance") val caTolerance: String,
        @ColumnInfo(name = "Cold Stratification Required") val coldStrat: String,
        @ColumnInfo(name = "Drought Tolerance") val droughtTolerance: String,
        @ColumnInfo(name = "Fertility Requirement") val fertilityReq: String,
        @ColumnInfo(name = "Fire Tolerance") val fireTolerance: String,
        @ColumnInfo(name = "Frost Free Days, Minimum") val frostFree: Int,
        @ColumnInfo(name = "Hedge Tolerance") val hedgeTolerance: String,
        @ColumnInfo(name = "Moisture Use") val moistureUse: String,
        @ColumnInfo(name = "pH (Minimum)") val phMin: Float,
        @ColumnInfo(name = "pH (Maximum)") val phMax: Float,
        @ColumnInfo(name = "Planting Density per Acre, Minimum") val densityMin: Int,
        @ColumnInfo(name = "Planting Density per Acre, Maximum") val densityMax: Int,
        @ColumnInfo(name = "Precipitation (Minimum)") val precipMin: Int,
        @ColumnInfo(name = "Precipitation (Maximum)") val precipMax: Int,
        @ColumnInfo(name = "Root Depth, Minimum (inches)") val rootMin: Int,
        @ColumnInfo(name = "Salinity Tolerance") val salinityTolerance: String,
        @ColumnInfo(name = "Shade Tolerance") val shadeTolerance: String,
        @ColumnInfo(name = "Temperature, Minimum (F)") val tempMin: Int
        )

@Dao
interface plantDao{
    @Query("SELECT * FROM Plants")
    fun getAll(): LiveData<List<PlantModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlant(plant:PlantModel)

}