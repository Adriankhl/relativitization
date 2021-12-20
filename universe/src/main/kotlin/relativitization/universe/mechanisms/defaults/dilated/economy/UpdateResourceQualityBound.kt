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
        val maxFirstClassQualityBoundRatio: Double = 5.0
        val maxSecondClassQualityBoundRatio: Double = 5.0
        val minClassQualityBoundRatio: Double = 1.01
        val qualityBoundChangeFactor: Double = 1.2

        ResourceType.values().forEach { resourceType ->
            val qualityMap: Map<ResourceQualityClass, MutableSingleResourceData> =
                ResourceQualityClass.values().map { resourceQualityClass ->
                    resourceQualityClass to mutablePlayerData.playerInternalData.economyData()
                        .resourceData.getSingleResourceData(resourceType, resourceQualityClass)
                }.toMap()

            val totalAmount: Double = qualityMap.values.fold(0.0) { acc, mutableSingleResourceData ->
                acc + mutableSingleResourceData.resourceAmount.total()
            }

            // Change quality bound

            // Third class always has bound = 0
            qualityMap.getValue(ResourceQualityClass.THIRD).resourceQualityLowerBound =
                MutableResourceQualityData()

            if (totalAmount > 0.0) {
                val firstRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.FIRST
                ).resourceAmount.total() / totalAmount
                val secondRatio: Double = qualityMap.getValue(
                    ResourceQualityClass.SECOND
                ).resourceAmount.total() / totalAmount

                if (secondRatio > idealSecondClassAmountRatio) {

                } else if (secondRatio < idealSecondClassAmountRatio) {

                }
            }
        }

        return listOf()
    }

}