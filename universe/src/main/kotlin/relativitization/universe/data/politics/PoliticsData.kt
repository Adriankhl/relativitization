package relativitization.universe.data.politics

import kotlinx.serialization.Serializable

@Serializable
data class PoliticsData(
    val governmentType: GovernmentType = GovernmentType.DICTATORSHIP
) {
    fun ideologyDistance(politicsData: PoliticsData): Double {
        return 0.0
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