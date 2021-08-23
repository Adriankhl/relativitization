package relativitization.universe.ai.default.consideration

import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.DualUtilityData
import relativitization.universe.data.subsystem.DiplomacyData
import kotlin.math.exp

/**
 * Consideration of diplomatic relation
 */
class RelationConsideration(
    val playerId: Int,
    val diplomacyData: DiplomacyData,
    private val rank: Int = 1,
    private val multiplier: Double = 1.0,
    private val normalizeRelation: Double = 100.0,
) : Consideration {

    // Return a dual utility by taking exponent of the relation divided by a normalization variable
    override fun getDualUtilityData(): DualUtilityData {
        return DualUtilityData(
            rank = rank,
            multiplier = multiplier,
            addend = exp(diplomacyData.getRelation(playerId).toDouble() / normalizeRelation),
        )
    }
}