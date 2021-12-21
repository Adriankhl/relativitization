package relativitization.universe.mechanisms.defaults.dilated.economy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.MutableSingleResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateResourceQualityBound : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        // Determine the maximum amount ratio of resource quality class
        val idealFirstClassAmountRatio: Double = 0.1
        val idealSecondClassAmountRatio: Double = 0.3
        val amountChangeFactor: Double = 0.2
        // Determine how the bound should change
        val maxClassQualityBoundRatio: Double = 5.0
        val minQualityClassDiff: Double = 0.1
        val qualityBoundChangeFactor: Double = 1.2

        ResourceType.values().forEach { resourceType ->
            val qualityMap: Map<ResourceQualityClass, MutableSingleResourceData> =
                ResourceQualityClass.values().map { resourceQualityClass ->
                    resourceQualityClass to mutablePlayerData.playerInternalData.economyData()
                        .resourceData.getSingleResourceData(resourceType, resourceQualityClass)
                }.toMap()

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
                val secondRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.SECOND
                ).resourceAmount.total() / totalAmount

                val firstRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.FIRST
                ).resourceAmount.total() / totalAmount


                // Put higher quality resource to lower quality class if there are too many
                // Do first class first
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

                // Calculate the quality bound of second class
                // Do second class first
                val newSecondQualityBound: MutableResourceQualityData = computeQualityBound(
                    currentBound = qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceQualityLowerBound,
                    amountRatio = secondRatio,
                    idealAmountRatio = idealSecondClassAmountRatio,
                    qualityBoundChangeFactor = qualityBoundChangeFactor,
                    minQualityBound = qualityMap.getValue(
                        ResourceQualityClass.THIRD
                    ).resourceQuality + minQualityClassDiff,
                    maxQualityBound = (qualityMap.getValue(
                        ResourceQualityClass.THIRD
                    ).resourceQuality + minQualityClassDiff) * maxClassQualityBoundRatio,
                )

                // Calculate the quality bound of first class
                val newFirstQualityBound: MutableResourceQualityData = computeQualityBound(
                    currentBound = qualityMap.getValue(
                        ResourceQualityClass.FIRST
                    ).resourceQualityLowerBound,
                    amountRatio = firstRatio,
                    idealAmountRatio = idealFirstClassAmountRatio,
                    qualityBoundChangeFactor = qualityBoundChangeFactor,
                    minQualityBound = qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceQuality + minQualityClassDiff,
                    maxQualityBound = (qualityMap.getValue(
                        ResourceQualityClass.SECOND
                    ).resourceQuality + minQualityClassDiff) * maxClassQualityBoundRatio,
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
        qualityBoundChangeFactor: Double,
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