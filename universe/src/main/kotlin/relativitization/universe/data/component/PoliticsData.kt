package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
@SerialName("PoliticsData")
data class PoliticsData(
    val centralizationLevel: Int = 0,
    val allowSubordinateBuildFactory: Boolean = false,
    val allowLeaderBuildLocalFactory: Boolean = true,
    val allowForeignInvestor: Boolean = true,
) : PlayerDataComponent() {
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

        val allowForeignInvestorDistance =
            if (allowForeignInvestor == politicsData.allowForeignInvestor) {
                0.0
            } else {
                1.0
            }
        return (centralizationDistance +
                allowSubordinateBuildFactoryDistance +
                allowForeignInvestorDistance
                )
    }
}

@Serializable
@SerialName("PoliticsData")
data class MutablePoliticsData(
    var centralizationLevel: Int = 0,
    var allowSubordinateBuildFactory: Boolean = false,
    var allowLeaderBuildLocalFactory: Boolean = true,
    var allowForeignInvestor: Boolean = true,
) : MutablePlayerDataComponent()