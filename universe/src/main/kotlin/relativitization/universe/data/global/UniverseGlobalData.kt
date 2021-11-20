package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.global.components.default.science.UniverseScienceData

@Serializable
data class UniverseGlobalData(
    val universeScienceData: UniverseScienceData = UniverseScienceData()
) {
    fun getScienceData() = universeScienceData
}

@Serializable
data class MutableUniverseGlobalData(
    var universeScienceData: UniverseScienceData = UniverseScienceData()
) {
    fun getScienceData() = universeScienceData

    fun updateScienceData(newUniverseScienceData: UniverseScienceData) {
        universeScienceData = newUniverseScienceData
    }
}