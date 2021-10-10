package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutableUniverseScienceData
import relativitization.universe.data.UniverseScienceData

@Serializable
data class UniverseGlobalData(
    val universeScienceData: UniverseScienceData = UniverseScienceData()
)

@Serializable
data class MutableUniverseGlobalData(
    val universeScienceData: MutableUniverseScienceData = MutableUniverseScienceData()
)