package relativitization.universe.data.subsystem.politics

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class PoliticsData(
    val centralizationLevel: Int = 0,
    val allowSubordinateBuildFactory: Boolean = false,
) {
    /**
     * Compute the ideology distance between player to represent how different between the two
     */
    fun ideologyDistance(politicsData: PoliticsData): Double {
        val centralizationDistance: Double =
            abs(centralizationLevel - politicsData.centralizationLevel).toDouble()
        val allowSubordinateBuildFactoryDistance =
            if (allowSubordinateBuildFactory == politicsData.allowSubordinateBuildFactory) {
                0.0
            } else {
                1.0
            }
        return centralizationDistance + allowSubordinateBuildFactoryDistance
    }
}

@Serializable
data class MutablePoliticsData(
    var centralizationLevel: Int = 0,
    val allowSubordinateBuildFactory: Boolean = false,
)