package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.abs

/**
 * Data related to politics
 *
 * @property agreeMerge agree remove this player and merge carriers to direct leader
 * @property centralizationLevel describe the centralization of the player, currently unused
 * @property allowSubordinateBuildFactory whether subordinate is allowed to build factory
 * @property allowLeaderBuildLocalFactory whether leader of a player is allowed to build local factory in the player
 * @property allowForeignInvestor whether foreign player is allowed to build factory
 */
@Serializable
@SerialName("PoliticsData")
data class PoliticsData(
    val agreeMerge: Boolean = false,
    val centralizationLevel: Int = 0,
    val allowSubordinateBuildFactory: Boolean = false,
    val allowLeaderBuildLocalFactory: Boolean = true,
    val allowForeignInvestor: Boolean = true,
) : DefaultPlayerDataComponent() {
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
    var agreeMerge: Boolean = false,
    var centralizationLevel: Int = 0,
    var allowSubordinateBuildFactory: Boolean = false,
    var allowLeaderBuildLocalFactory: Boolean = true,
    var allowForeignInvestor: Boolean = true,
) : MutableDefaultPlayerDataComponent()