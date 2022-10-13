package relativitization.universe.ai.defaults.node.self.movement

import relativitization.universe.ai.defaults.consideration.event.HasMovementTargetConsideration
import relativitization.universe.ai.defaults.consideration.position.EnemyNeighbourConsideration
import relativitization.universe.ai.defaults.consideration.position.FightingEnemyConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientFuelMaxSpeedConsideration
import relativitization.universe.ai.defaults.consideration.population.HigherPopulationDensityThenNeighborCubeConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Movement
import kotlin.math.min
import kotlin.random.Random

class MovementReasoner(private val random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveToLowerDensityCubeOption(random),
        MoveToEnemyOption(random),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
    )
}

/**
 * Move to a neighbouring cube with lower density
 */
class MoveToLowerDensityCubeOption(private val random: Random) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        HigherPopulationDensityThenNeighborCubeConsideration(
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        SufficientFuelMaxSpeedConsideration(
            playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            maxSpeed = 0.1,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        HasMovementTargetConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0

        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val otherPopulation: Double = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourInCube(1).fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
            }

        val allNeighborCube: List<Int3D> = planDataAtPlayer.universeData3DAtPlayer
            .getInt3DAtCubeSurface(2)

        val neighborCubeWithLowerPopulation: List<Int3D> = allNeighborCube.filter {
            val populationAtCube: Double =
                planDataAtPlayer.universeData3DAtPlayer.get(it).values.flatten().fold(
                    0.0
                ) { acc, playerData ->
                    acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
                }
            populationAtCube < otherPopulation
        }
        if (neighborCubeWithLowerPopulation.isNotEmpty()) {
            val targetInt3D: Int3D = neighborCubeWithLowerPopulation[random.nextInt(
                0,
                neighborCubeWithLowerPopulation.size
            )]

            val event = MoveToDouble3DEvent(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetDouble3D = targetInt3D.toDouble3DCenter(),
                maxSpeed = 0.1
            )

            planDataAtPlayer.addCommand(
                AddEventCommand(
                    event = event,
                )
            )
        }
    }
}

/**
 * Move to a cube with enemy
 */
class MoveToEnemyOption(private val random: Random) : DualUtilityOption() {
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
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        FightingEnemyConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        SufficientFuelMaxSpeedConsideration(
            playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            maxSpeed = 0.1,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        HasMovementTargetConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
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

            // Estimate max. speed possible
            val maxSpeedEstimate: Double = Movement.maxSpeedSimpleEstimation(
                initialRestMass = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.physicsData()
                    .totalRestMass(),
                initialVelocity = planDataAtPlayer.getCurrentMutablePlayerData().velocity.toVelocity(),
                movementFuelRestMass = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
                    .physicsData().fuelRestMassData.movement,
                speedOfLight = planDataAtPlayer.universeData3DAtPlayer.universeSettings.speedOfLight
            )

            val maxSpeed: Double = min(maxSpeedEstimate, 0.9)

            val event = MoveToDouble3DEvent(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetDouble3D = enemy.double4D.toDouble3D(),
                maxSpeed = maxSpeed,
            )

            planDataAtPlayer.addCommand(
                AddEventCommand(
                    event = event,
                )
            )
        }
    }
}