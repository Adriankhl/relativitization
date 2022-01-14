package relativitization.universe.ai.defaults.node.self.movement

import relativitization.universe.ai.defaults.consideration.event.HasMovementEventConsideration
import relativitization.universe.ai.defaults.consideration.population.HigherPopulationDensityThenNeighborCubeConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.defaults.physics.Double3D
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.random.Rand

class MovementReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        MoveToLowerDensityCubeOption(),
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
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val totalPopulation: Double = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbourAndSelf(0).fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
            }

        val allNeighborCube: List<Int3D> = planDataAtPlayer.universeData3DAtPlayer
            .getInt3DAtCubeSurface(
                1
            )

        val neighborCubeWithHigherPopulation: List<Int3D> = allNeighborCube.filter {
            val populationAtCube: Double =
                planDataAtPlayer.universeData3DAtPlayer.get(it).values.flatten().fold(
                    0.0
                ) { acc, playerData ->
                    acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
                }
            populationAtCube < totalPopulation
        }
        if (neighborCubeWithHigherPopulation.isNotEmpty()) {
            val targetInt3D: Int3D = neighborCubeWithHigherPopulation[Rand.rand().nextInt(
                0,
                neighborCubeWithHigherPopulation.size
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