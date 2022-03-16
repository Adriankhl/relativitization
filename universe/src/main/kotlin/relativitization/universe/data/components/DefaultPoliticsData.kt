package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import kotlin.math.abs

/**
 * Data related to politics
 *
 * @property hasAgreedMerge agree remove this player and merge carriers to direct leader
 * @property centralizationLevel describe the centralization of the player, currently unused
 * @property isSubordinateBuildFactoryAllowed whether subordinate is allowed to build factory
 * @property isLeaderBuildLocalFactoryAllowed whether leader of a player is allowed to build local factory in the player
 * @property isForeignInvestorAllowed whether foreign player is allowed to build factory
 */
@Serializable
@SerialName("PoliticsData")
data class PoliticsData(
    val hasAgreedMerge: Boolean = false,
    val centralizationLevel: Int = 0,
    val isSubordinateBuildFactoryAllowed: Boolean = false,
    val isLeaderBuildLocalFactoryAllowed: Boolean = true,
    val isForeignInvestorAllowed: Boolean = true,
) : DefaultPlayerDataComponent() {
    /**
     * Compute the ideology distance between player to represent how different between the two
     */
    fun ideologyDistance(politicsData: PoliticsData): Double {
        val centralizationDistance: Double =
            abs(centralizationLevel - politicsData.centralizationLevel).toDouble()
        val isSubordinateBuildFactoryAllowedDistance =
            if (isSubordinateBuildFactoryAllowed == politicsData.isSubordinateBuildFactoryAllowed) {
                0.0
            } else {
                1.0
            }

        val isForeignInvestorAllowedDistance =
            if (isForeignInvestorAllowed == politicsData.isForeignInvestorAllowed) {
                0.0
            } else {
                1.0
            }
        return (centralizationDistance +
                isSubordinateBuildFactoryAllowedDistance +
                isForeignInvestorAllowedDistance
                )
    }
}

@Serializable
@SerialName("PoliticsData")
data class MutablePoliticsData(
    var hasAgreedMerge: Boolean = false,
    var centralizationLevel: Int = 0,
    var isSubordinateBuildFactoryAllowed: Boolean = false,
    var isLeaderBuildLocalFactoryAllowed: Boolean = true,
    var isForeignInvestorAllowed: Boolean = true,
) : MutableDefaultPlayerDataComponent()

fun PlayerInternalData.politicsData(): PoliticsData =
    playerDataComponentMap.getOrDefault(PoliticsData::class, PoliticsData())

fun MutablePlayerInternalData.politicsData(): MutablePoliticsData =
    playerDataComponentMap.getOrDefault(MutablePoliticsData::class, MutablePoliticsData())

fun MutablePlayerInternalData.politicsData(newPoliticsData: MutablePoliticsData) =
    playerDataComponentMap.put(newPoliticsData)