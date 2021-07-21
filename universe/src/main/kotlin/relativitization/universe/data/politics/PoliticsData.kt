package relativitization.universe.data.politics

import kotlinx.serialization.Serializable

@Serializable
data class PoliticsData(
    val governmentType: GovernmentType = GovernmentType.DICTATORSHIP
) {
    /**
     * Compute the ideology distance between player to represent how different between the two
     */
    fun ideologyDistance(politicsData: PoliticsData): Double {
        val governmentTypeDistance: Double = if (governmentType == politicsData.governmentType) {
            0.0
        } else {
            1.0
        }
        return governmentTypeDistance
    }
}

@Serializable
data class MutablePoliticsData(
    var governmentType: GovernmentType = GovernmentType.DICTATORSHIP
)

enum class GovernmentType {
    DEMOCRACY,
    DICTATORSHIP,
}