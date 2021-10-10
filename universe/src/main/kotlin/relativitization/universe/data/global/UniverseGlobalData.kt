package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutableUniverseScienceData
import relativitization.universe.data.UniverseScienceData

@Serializable
data class UniverseGlobalData(
    var universeScienceData: UniverseScienceData = UniverseScienceData()
) {
    fun getScienceData() = universeScienceData
}