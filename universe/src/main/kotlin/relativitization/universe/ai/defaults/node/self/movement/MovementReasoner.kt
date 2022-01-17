package relativitization.universe.ai.defaults.node.self.movement

import relativitization.universe.ai.defaults.consideration.enemy.EnemyNeighbourConsideration
import relativitization.universe.ai.defaults.consideration.enemy.FightingEnemyConsideration
import relativitization.universe.ai.defaults.consideration.event.HasMovementEventConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientFuelMoveToDouble3DConsideration
import relativitization.universe.ai.defaults.consideration.population.HigherPopulationDensityThenNeighborCubeConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.random.Rand

class MovementReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveToLowerDensityCubeOption(),
        MoveToEnemyOption(),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

/**
 * Move to a neighbouring cube with lower density
 */
class MoveToLowerDensityCubeOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        HasMovementEventConsideration(rankIfTrue = 0, multiplierIfTrue = 0.0, bonusIfTrue = 0.0),
        HigherPopulationDensityThenNeighborCubeConsideration(
            rankIfTrue = 1,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 1.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
        SufficientFuelMoveToDouble3DConsideration(
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
        val otherPopulation: Double = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbour(0).fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
            }

        val allNeighborCube: List<Int3D> = planDataAtPlayer.universeData3DAtPlayer
            .getInt3DAtCubeSurface(
                1
            )

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
            val targetInt3D: Int3D = neighborCubeWithLowerPopulation[Rand.rand().nextInt(
                0,
                neighborCubeWithLowerPopulation.size
            )]

            val event = MoveToDouble3DEvent(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                stayTime = 99,
                targetDouble3D = targetInt3D.toDouble3DCenter(),
                maxSpeed = 0.1
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

/**
 * Move to a cube with enemy
 */
class MoveToEnemyOption : DualUtilityOption() {
    val range: Int = 2

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        HasMovementEventConsideration(rankIfTrue = 0, multiplierIfTrue = 0.0, bonusIfTrue = 0.0),
        EnemyNeighbourConsideration(
            range = range,
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
        SufficientFuelMoveToDouble3DConsideration(
            maxSpeed = 0.1,
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0
        ),
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val neighbour: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(range)

        if (neighbour.isNotEmpty()) {
            val enemy: PlayerData = neighbour[Rand.rand().nextInt(neighbour.size)]

            val event = MoveToDouble3DEvent(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                stayTime = 99,
                targetDouble3D = enemy.double4D.toDouble3D(),
                maxSpeed = 0.1,
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