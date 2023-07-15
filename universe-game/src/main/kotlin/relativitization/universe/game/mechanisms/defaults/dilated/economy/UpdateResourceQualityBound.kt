package relativitization.universe.game.mechanisms.defaults.dilated.economy

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.MutableSingleResourceData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.geq
import relativitization.universe.game.data.components.defaults.economy.getSingleResourceData
import relativitization.universe.game.data.components.defaults.economy.leq
import relativitization.universe.game.data.components.defaults.economy.plus
import relativitization.universe.game.data.components.defaults.economy.times
import relativitization.universe.game.data.components.defaults.economy.total
import relativitization.universe.game.data.components.economyData
import kotlin.random.Random

object UpdateResourceQualityBound : Mechanism() {
    // Parameters
    // Determine the maximum amount ratio of resource quality class
    private const val idealFirstClassAmountRatio: Double = 0.1
    private const val idealSecondClassAmountRatio: Double = 0.3
    private const val amountChangeFactor: Double = 0.2
    // Determine how the bound should change
    private const val maxClassQualityBoundRatio: Double = 5.0
    private const val minQualityClassDiff: Double = 0.1
    private const val qualityBoundChangeFactor: Double = 1.2

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        ResourceType.entries.forEach { resourceType ->
            val qualityMap: Map<ResourceQualityClass, MutableSingleResourceData> =
                ResourceQualityClass.entries.associateWith { resourceQualityClass ->
                    mutablePlayerData.playerInternalData.economyData()
                        .resourceData.getSingleResourceData(resourceType, resourceQualityClass)
                }

            val totalAmount: Double =
                qualityMap.values.fold(0.0) { acc, mutableSingleResourceData ->
                    acc + mutableSingleResourceData.resourceAmount.total()
                }

            // Change quality bound

            // Third class always has bound = 0
            qualityMap.getValue(ResourceQualityClass.THIRD).resourceQualityLowerBound =
                MutableResourceQualityData()

            // Only do the update when there is resource
            if (totalAmount > 0.0) {

                val firstRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.FIRST
                ).resourceAmount.total() / totalAmount

                // Put higher quality resource to lower quality class if there are too many

                // Compute first class before second class
                if (firstRatio > idealFirstClassAmountRatio) {
                    val changeFraction: Double = amountChangeFactor *
                            (firstRatio - idealFirstClassAmountRatio) / firstRatio
                    val changeAmount: Double = qualityMap.getValue(
                        ResourceQualityClass.FIRST
                    ).resourceAmount.total() * changeFraction

                    // Update second class resource
                    qualityMap.getValue(
                        ResourceQualityClass.FIRST
                    ).resourceAmount *= (1.0 - changeFraction)

                    // Add the amount to second class resource
                    qualityMap.getValue(ResourceQualityClass.SECOND).addResource(
                        newResourceQuality = qualityMap.getValue(
                            ResourceQualityClass.FIRST
                        ).resourceQuality,
                        newResourceAmount = changeAmount
                    )
                }

                val secondRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.SECOND
                ).resourceAmount.total() / totalAmount

                if (secondRatio > idealSecondClassAmountRatio) {
                    val changeFraction: Double = amountChangeFactor *
                            (secondRatio - idealSecondClassAmountRatio) / secondRatio
                    val changeAmount: Double = qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceAmount.total() * changeFraction

                    // Update second class resource
                    qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceAmount *= (1.0 - changeFraction)

                    // Add the amount to third class resource
                    qualityMap.getValue(ResourceQualityClass.THIRD).addResource(
                        newResourceQuality = qualityMap.getValue(
                            ResourceQualityClass.SECOND
                        ).resourceQuality,
                        newResourceAmount = changeAmount
                    )
                }

                // The minimum of second quality bound
                val minSecondQualityBound: MutableResourceQualityData =
                    qualityMap.getValue(ResourceQualityClass.THIRD).resourceQuality.combineMax(
                        qualityMap.getValue(ResourceQualityClass.THIRD).resourceQualityLowerBound
                    ) + minQualityClassDiff

                // Calculate the quality bound of second class
                // Do second class first
                val newSecondQualityBound: MutableResourceQualityData = computeQualityBound(
                    currentBound = qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceQualityLowerBound,
                    amountRatio = secondRatio,
                    idealAmountRatio = idealSecondClassAmountRatio,
                    minQualityBound = minSecondQualityBound,
                    maxQualityBound = minSecondQualityBound * maxClassQualityBoundRatio,
                )

                // The minimum of second quality bound
                val minFirstQualityBound: MutableResourceQualityData =
                    qualityMap.getValue(ResourceQualityClass.SECOND).resourceQuality.combineMax(
                        qualityMap.getValue(ResourceQualityClass.SECOND).resourceQualityLowerBound
                    ) + minQualityClassDiff

                // Calculate the quality bound of first class
                val newFirstQualityBound: MutableResourceQualityData = computeQualityBound(
                    currentBound = qualityMap.getValue(
                        ResourceQualityClass.FIRST
                    ).resourceQualityLowerBound,
                    amountRatio = firstRatio,
                    idealAmountRatio = idealFirstClassAmountRatio,
                    minQualityBound = minFirstQualityBound,
                    maxQualityBound = minFirstQualityBound * maxClassQualityBoundRatio,
                )


                // Update the bound
                qualityMap.getValue(ResourceQualityClass.SECOND).resourceQualityLowerBound =
                    newSecondQualityBound
                qualityMap.getValue(ResourceQualityClass.FIRST).resourceQualityLowerBound =
                    newFirstQualityBound

            }
        }


        return listOf()
    }

    private fun computeQualityBound(
        currentBound: MutableResourceQualityData,
        amountRatio: Double,
        idealAmountRatio: Double,
        minQualityBound: MutableResourceQualityData,
        maxQualityBound: MutableResourceQualityData,
    ): MutableResourceQualityData {
        val newQualityBound: MutableResourceQualityData = when {
            amountRatio > idealAmountRatio -> {
                currentBound * qualityBoundChangeFactor
            }
            amountRatio < idealAmountRatio -> {
                currentBound * (1.0 / qualityBoundChangeFactor)
            }
            else -> {
                currentBound * 1.0
            }
        }

        return when {
            newQualityBound.geq(maxQualityBound) -> maxQualityBound.copy()
            newQualityBound.leq(minQualityBound) -> minQualityBound.copy()
            else -> newQualityBound
        }
    }
}