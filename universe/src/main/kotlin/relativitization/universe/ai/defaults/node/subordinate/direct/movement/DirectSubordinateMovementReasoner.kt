package relativitization.universe.ai.defaults.node.subordinate.direct.movement

import relativitization.universe.ai.defaults.consideration.position.EnemyNeighbourConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientFuelMaxSpeedConsideration
import relativitization.universe.ai.defaults.consideration.military.InWarConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Movement
import kotlin.math.min
import kotlin.random.Random

class DirectSubordinateMovementReasoner(
    private val directSubordinateId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveDirectSubordinateToNearbyEnemyOption(
            directSubordinateId = directSubordinateId,
            random = random,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
            random = random,
        ),
    )
}

/**
 * Move direct subordinate to a nearby enemy
 */
class MoveDirectSubordinateToNearbyEnemyOption(
    private val directSubordinateId: Int,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        EnemyNeighbourConsideration(
            range = 3,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        InWarConsideration(
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        SufficientFuelMaxSpeedConsideration(
            playerId = directSubordinateId,
            maxSpeed = 0.1,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val enemyIdSet: Set<Int> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .diplomacyData().relationData.enemyIdSet.filter {
                planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
            }.toSet()


        if (enemyIdSet.isNotEmpty()) {
            // map from enemy id to integer distance
            val enemyDistanceMap: Map<Int, Int> = enemyIdSet.associateWith {
                val enemyData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(it)
                Intervals.intDistance(
                    enemyData.int4D,
                    planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D()
                )
            }
            val closestDistance: Int = enemyDistanceMap.values.minOf { it }
            val closestEnemyList: List<Int> = enemyDistanceMap.filterValues {
                it == closestDistance
            }.keys.toList()

            val enemyId: Int = closestEnemyList[random.nextInt(closestEnemyList.size)]
            val enemy: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(enemyId)

            // Estimate max. speed possible, only consider 0.5 of the total movement fuel
            // to reduce the chance that this command not being executed
            val maxSpeedEstimate: Double = Movement.maxSpeedSimpleEstimation(
                initialRestMass = planDataAtPlayer.universeData3DAtPlayer.get(directSubordinateId)
                    .playerInternalData.physicsData().totalRestMass(),
                initialVelocity = planDataAtPlayer.universeData3DAtPlayer.get(directSubordinateId)
                    .velocity,
                movementFuelRestMass = planDataAtPlayer.universeData3DAtPlayer
                    .get(directSubordinateId).playerInternalData.physicsData()
                    .fuelRestMassData.movement * 0.5,
                speedOfLight = planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight
            )

            val maxSpeed: Double = min(maxSpeedEstimate, 0.9)

            val event = MoveToDouble3DEvent(
                toId = directSubordinateId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetDouble3D = enemy.double4D.toDouble3D(),
                maxSpeed = maxSpeed,
            )

            planDataAtPlayer.addCommand(
                AddEventCommand(
                    event = event,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                )
            )
        }
    }
}