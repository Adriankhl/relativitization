package relativitization.universe.ai.defaults.node.subordinate.direct.movement

import relativitization.universe.ai.defaults.consideration.enemy.EnemyNeighbourConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientFuelMaxSpeedConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Movement
import relativitization.universe.maths.random.Rand
import kotlin.math.min

class DirectSubordinateMovementReasoner(
    private val directSubordinateId: Int
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveDirectSubordinateToEnemyOption(directSubordinateId),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

class MoveDirectSubordinateToEnemyOption(
    private val directSubordinateId: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        EnemyNeighbourConsideration(
            range = 2,
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
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
            .diplomacyData().relationMap.filterValues {
                it.diplomaticRelationState == DiplomaticRelationState.ENEMY
            }.keys


        if (enemyIdSet.isNotEmpty()) {
            // map from enemy id to integer distance
            val enemyDistanceMap: Map<Int, Int> = enemyIdSet.associateWith {
                val enemyData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(it)
                Intervals.intDistance(enemyData.int4D, planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D())
            }
            val closestDistance: Int = enemyDistanceMap.values.minOf { it }
            val closestEnemyList: List<Int> = enemyDistanceMap.filterValues { it == closestDistance }.keys.toList()

            val enemyId: Int = closestEnemyList[Rand.rand().nextInt(closestEnemyList.size)]
            val enemy: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(enemyId)

            // Estimate max. speed possible, only consider 0.5 of the total movement fuel
            // to reduce the chance that this command not being executed
            val maxSpeedEstimate: Double = Movement.maxSpeedSimpleEstimation(
                initialRestMass = planDataAtPlayer.universeData3DAtPlayer.get(directSubordinateId)
                    .playerInternalData.physicsData().totalRestMass(),
                initialVelocity = planDataAtPlayer.universeData3DAtPlayer.get(directSubordinateId).velocity,
                movementFuelRestMass = planDataAtPlayer.universeData3DAtPlayer.get(directSubordinateId)
                    .playerInternalData.physicsData().fuelRestMassData.movement * 0.5,
                speedOfLight = planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight
            )

            val maxSpeed: Double = min(maxSpeedEstimate, 0.9)

            val event = MoveToDouble3DEvent(
                toId = directSubordinateId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                stayTime = 99,
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