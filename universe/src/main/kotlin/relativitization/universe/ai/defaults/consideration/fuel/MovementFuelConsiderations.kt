package relativitization.universe.ai.defaults.consideration.fuel

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.physics.Velocity
import relativitization.universe.maths.physics.Movement

/**
 * Check whether there is sufficient movement fuel to change the velocity
 *
 * @property playerId the id of the player to consider
 * @property maxSpeed the target max speed
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientFuelMaxSpeedConsideration(
    private val playerId: Int,
    private val maxSpeed: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val initialRestMass: Double = planDataAtPlayer.getMutablePlayerData(playerId).playerInternalData
            .physicsData().totalRestMass()

        val initialVelocity: Velocity = planDataAtPlayer.getMutablePlayerData(playerId).velocity.toVelocity()

        val movementFuelMass: Double = planDataAtPlayer.getMutablePlayerData(playerId).playerInternalData
            .physicsData().fuelRestMassData.movement

        val requiredDeltaMass: Double = Movement.requiredDeltaRestMassSimpleEstimation(
            initialRestMass = initialRestMass,
            initialVelocity = initialVelocity,
            maxSpeed = maxSpeed,
            speedOfLight = planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight,
        )

        val isMovementFuelSufficient: Boolean = movementFuelMass >= requiredDeltaMass

        return if (isMovementFuelSufficient) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}