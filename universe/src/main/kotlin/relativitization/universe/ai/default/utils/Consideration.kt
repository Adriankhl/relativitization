package relativitization.universe.ai.default.utils

import kotlin.math.max

interface Consideration {
    fun getDualUtilityData(): DualUtilityData
}

data class DualUtilityData(
    val addend: Double = 0.0,
    val multiplier: Double = 1.0,
    val rank: Int = 0,
) {
    fun combine(other: DualUtilityData): DualUtilityData {
        return DualUtilityData(
            addend + other.addend,
            multiplier * other.multiplier,
            max(rank, other.rank)
        )
    }
}