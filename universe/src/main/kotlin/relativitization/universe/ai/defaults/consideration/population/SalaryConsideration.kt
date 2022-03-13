package relativitization.universe.ai.defaults.consideration.population

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.popSystemData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.pow

/**
 * Check whether the salary of labourer in a foreign carrier compare to a reference salary
 *
 * @property otherPlayerId the id of another player
 * @property otherPlayerCarrierId the id of the carrier with the labourer
 * @property referenceSalary the salary to compare with
 * @property initialBonus the bonus if the salary is equal to the reference
 * @property exponent the exponent to scale the bonus
 * @property rank the rank of this consideration
 * @property multiplier the multiplier of this consideration
 */
class ForeignLabourerSalaryConsideration(
    private val otherPlayerId: Int,
    private val otherPlayerCarrierId: Int,
    private val referenceSalary: Double,
    private val initialBonus: Double,
    private val exponent: Double,
    private val rank: Int,
    private val multiplier: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val salary: Double = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)
            .playerInternalData.popSystemData().carrierDataMap.getValue(otherPlayerCarrierId)
            .allPopData.labourerPopData.commonPopData.salaryPerEmployee

        return if (referenceSalary > 0.0) {
            DualUtilityData(
                rank,
                multiplier,
                initialBonus * exponent.pow(salary / referenceSalary)
            )
        } else {
            logger.debug("reference salary <= 0.0")
            DualUtilityDataFactory.noImpact()
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}