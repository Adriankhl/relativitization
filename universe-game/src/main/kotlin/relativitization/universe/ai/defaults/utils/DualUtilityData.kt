package relativitization.universe.ai.defaults.utils

/**
 * For choosing between dual utility options
 */
data class DualUtilityData(
    val rank: Int,
    val multiplier: Double,
    val bonus: Double,
)

object DualUtilityDataFactory {
    /**
     * The dual utility data that has no impact on the option
     */
    fun noImpact(): DualUtilityData = DualUtilityData(
        rank = 0,
        multiplier = 1.0,
        bonus = 0.0
    )
}