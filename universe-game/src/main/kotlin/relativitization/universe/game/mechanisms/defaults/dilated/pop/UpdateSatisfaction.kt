package relativitization.universe.game.mechanisms.defaults.dilated.pop

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.div
import relativitization.universe.game.data.components.defaults.economy.mag
import relativitization.universe.game.data.components.defaults.economy.plus
import relativitization.universe.game.data.components.defaults.economy.times
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableResourceDesireData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.components.totalAdultPopulation
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object UpdateSatisfaction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // Parameters
        val satisfactionUpdateFactor = 0.5
        val satisfactionMaxIncreaseDiff = 3.0
        val averageDesireInputQualityRange = 3
        val unevennessMaxBonus = 0.2

        val averageDesireInputQualityMap: Map<ResourceType, ResourceQualityData> =
            computeAverageInputResourceQualityMap(
                mutablePlayerData,
                universeData3DAtPlayer,
                averageDesireInputQualityRange
            )

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.entries.forEach { popType ->
                val mutableCommonPopData: MutableCommonPopData =
                    carrier.allPopData.getCommonPopData(popType)

                updateSatisfaction(
                    mutableCommonPopData = mutableCommonPopData,
                    desireResourceTypeList = mutableCommonPopData.desireResourceMap.keys.toList(),
                    satisfactionUpdateFactor = satisfactionUpdateFactor,
                    satisfactionMaxIncreaseDiff = satisfactionMaxIncreaseDiff,
                    averageDesireInputQualityMap = averageDesireInputQualityMap,
                    unevennessMaxBonus = unevennessMaxBonus,
                )

                // Store and clear resource input
                mutableCommonPopData.lastResourceInputMap =
                    mutableCommonPopData.resourceInputMap.toMutableMap()
                mutableCommonPopData.resourceInputMap.clear()
            }
        }

        return listOf()
    }

    /**
     * Compute average quality of input desire resources in a neighbourhood
     * The value in the returned map equals to amount * quality / population
     */
    fun computeAverageInputResourceQualityMap(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        averageDesireInputQualityRange: Int,
    ): Map<ResourceType, ResourceQualityData> {
        val resourceMap: MutableMap<ResourceType, ResourceQualityData> = mutableMapOf()

        // Add the resource quality times the amount to resource map, then divide by total
        // population later
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.entries.forEach { popType ->
                carrier.allPopData.getCommonPopData(popType).lastResourceInputMap.forEach { (resourceType, desireData) ->
                    val newQualityData: ResourceQualityData = resourceMap.getOrDefault(
                        resourceType,
                        DataSerializer.copy(MutableResourceQualityData())
                    ) + (desireData.desireQuality * desireData.desireAmount)
                    resourceMap[resourceType] = newQualityData
                }
            }
        }

        val neighbors: List<PlayerData> =
            universeData3DAtPlayer.getNeighbourInCube(averageDesireInputQualityRange)

        neighbors.forEach { playerData ->
            playerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
                PopType.entries.forEach { popType ->
                    carrier.allPopData.getCommonPopData(popType).lastResourceInputMap.forEach { (resourceType, desireData) ->
                        val newQualityData: ResourceQualityData = resourceMap.getOrDefault(
                            resourceType,
                            DataSerializer.copy(MutableResourceQualityData())
                        ) + (desireData.desireQuality * desireData.desireAmount)
                        resourceMap[resourceType] = newQualityData
                    }
                }
            }
        }

        // compute the total population
        val totalPopulation: Double =
            mutablePlayerData.playerInternalData.popSystemData().totalAdultPopulation() +
                    neighbors.fold(0.0) { acc, playerData ->
                        acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
                    }

        return if (totalPopulation > 0.0) {
            resourceMap.mapValues { (_, qualityData) -> qualityData / totalPopulation }
        } else {
            mapOf()
        }
    }

    /**
     * Compute satisfaction, adjusted by time dilation
     */
    fun updateSatisfaction(
        mutableCommonPopData: MutableCommonPopData,
        desireResourceTypeList: List<ResourceType>,
        satisfactionUpdateFactor: Double,
        satisfactionMaxIncreaseDiff: Double,
        averageDesireInputQualityMap: Map<ResourceType, ResourceQualityData>,
        unevennessMaxBonus: Double,
    ) {
        val amountFractionList: List<Double> = desireResourceTypeList.map { resourceType ->
            val originalDesire: MutableResourceDesireData =
                mutableCommonPopData.desireResourceMap.getOrDefault(
                    resourceType,
                    MutableResourceDesireData()
                )

            val inputDesire: MutableResourceDesireData =
                mutableCommonPopData.resourceInputMap.getOrDefault(
                    resourceType,
                    MutableResourceDesireData()
                )

            if (originalDesire.desireAmount > 0.0) {
                inputDesire.desireAmount / (originalDesire.desireAmount)
            } else {
                1.0
            }
        }

        val qualityExponentDiffList: List<Double> = desireResourceTypeList.map { resourceType ->
            val originalDesire: MutableResourceDesireData =
                mutableCommonPopData.desireResourceMap.getOrDefault(
                    resourceType,
                    MutableResourceDesireData()
                )

            val inputDesire: MutableResourceDesireData =
                mutableCommonPopData.resourceInputMap.getOrDefault(
                    resourceType,
                    MutableResourceDesireData()
                )

            // Use exponential here since ideal factory quality is log2(technology)
            1.2.pow(inputDesire.desireQuality.mag() - originalDesire.desireQuality.mag())
        }

        // Compare the input quality to the neighbour input quality
        val unevennessDiffList: List<Double> = desireResourceTypeList.map { resourceType ->
            val inputDesire: MutableResourceDesireData =
                mutableCommonPopData.resourceInputMap.getOrDefault(
                    resourceType,
                    MutableResourceDesireData()
                )

            val averageQualityData: MutableResourceQualityData = inputDesire.desireQuality *
                    inputDesire.desireAmount / mutableCommonPopData.adultPopulation

            val otherAverageQualityData: ResourceQualityData =
                averageDesireInputQualityMap.getOrDefault(
                    resourceType,
                    DataSerializer.copy(MutableResourceQualityData())
                )

            averageQualityData.mag() - otherAverageQualityData.mag()
        }

        val originalSatisfaction: Double = mutableCommonPopData.satisfaction


        val amountFactor: Double = amountFractionList.minOfOrNull { it } ?: 1.0

        // Modify quality factor based on amount factor
        // If amount factor is small, the impact from quality should be small
        val qualityFactorWithoutAmountConsideration: Double =
            qualityExponentDiffList.minOfOrNull { it } ?: 1.0
        val qualityFactor: Double = if (amountFactor > 1.0) {
            qualityFactorWithoutAmountConsideration
        } else {
            (qualityFactorWithoutAmountConsideration - 1.0) * amountFactor + 1.0
        }

        // Calculate the bonus factor from unevenness, compare to a reference magnitude
        val unevennessDiffReference = 5.0
        val unevennessDiff: Double = if (unevennessDiffList.isNotEmpty()) {
            unevennessDiffList.fold(0.0) { acc, d ->
                acc + d
            } / unevennessDiffList.size
        } else {
            0.0
        }
        val unevennessBonus: Double = when {
            unevennessDiff > unevennessDiffReference -> unevennessMaxBonus
            unevennessDiff < -unevennessDiffReference -> -unevennessMaxBonus
            else -> unevennessMaxBonus * unevennessDiff / unevennessDiffReference
        }

        // the ideal satisfaction which the population should be at
        val overallFactor: Double = amountFactor * qualityFactor + unevennessBonus
        val idealSatisfaction: Double = if (overallFactor < 0.0) {
            0.0
        } else {
            overallFactor
        }

        // Compute the change of satisfaction by piecewise function
        val deltaSatisfaction: Double = if (idealSatisfaction > originalSatisfaction) {
            min(
                (idealSatisfaction - originalSatisfaction) * satisfactionUpdateFactor,
                satisfactionMaxIncreaseDiff
            )
        } else {
            (idealSatisfaction - originalSatisfaction) * satisfactionUpdateFactor
        }

        mutableCommonPopData.satisfaction += deltaSatisfaction
    }
}