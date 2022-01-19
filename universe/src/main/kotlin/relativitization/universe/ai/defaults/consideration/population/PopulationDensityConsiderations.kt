package relativitization.universe.ai.defaults.consideration.population

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.physics.Int3D

/**
 * Check if the population in the cube is higher than any of the neighboring cube
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class HigherPopulationDensityThenNeighborCubeConsideration(
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
        // All population in the cube, exclude self
        val otherPopulation: Double = planDataAtPlayer.universeData3DAtPlayer
            .getNeighbour(0).fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
            }

        val allNeighborCube: List<Int3D> = planDataAtPlayer.universeData3DAtPlayer
            .getInt3DAtCubeSurface(
                1
            )

        val allNeighborPopulation: List<Double> = allNeighborCube.map { int3D ->
            planDataAtPlayer.universeData3DAtPlayer.get(int3D).values.flatten().fold(
                0.0
            ) { acc, playerData ->
                acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
            }
        }

        val isHigherDensity: Boolean = allNeighborPopulation.any { otherPopulation > it }

        return if (isHigherDensity) {
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