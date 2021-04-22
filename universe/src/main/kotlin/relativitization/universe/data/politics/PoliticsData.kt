package relativitization.universe.data.politics

import kotlinx.serialization.Serializable

@Serializable
data class PoliticsData(
    val governmentType: GovernmentType = GovernmentType.DICTATORSHIP
)

@Serializable
data class MutablePoliticsData(
    var governmentType: GovernmentType = GovernmentType.DICTATORSHIP
)

enum class GovernmentType {
    DEMOCRACY,
    DICTATORSHIP,
}