package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableEconomyData
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Pop buy resource to fulfill their desire
 * Can buy from the player which the pop belongs to only
 * Will implement buying from other player in the future
 */
object PopBuyResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            PopType.values().forEach { popType ->
                val commonPopData = mutableCarrierData.allPopData.getCommonPopData(popType)
                buyFromThisPlayer(
                    mutablePlayerData.playerInternalData.physicsData(),
                    commonPopData,
                    mutablePlayerData.playerInternalData.economyData(),
                )
            }
        }

        return listOf()
    }

    fun buyFromThisPlayer(
        physicsData: MutablePhysicsData,
        commonPopData: MutableCommonPopData,
        economyData: MutableEconomyData,
    ) {
        val numDesire: Int = commonPopData.desireResourceMap.size

        if (numDesire > 0) {
            val availableFuelPerResource: Double = commonPopData.saving / numDesire

            commonPopData.desireResourceMap.forEach { (resourceType, desireData) ->
                val selectedClass: ResourceQualityClass =
                    economyData.resourceData.tradeQualityClass(
                        resourceType,
                        desireData.desireAmount,
                        desireData.desireQuality,
                        availableFuelPerResource,
                    )

                val selectedQuality: MutableResourceQualityData =
                    economyData.resourceData.getResourceQuality(
                        resourceType = resourceType,
                        resourceQualityClass = selectedClass,
                    )

                val selectedPrice: Double = economyData.resourceData.getResourcePrice(
                    resourceType = resourceType,
                    resourceQualityClass = selectedClass
                )

                val totalAmount: Double = if (selectedPrice > 0.0) {
                    listOf(
                        desireData.desireAmount,
                        economyData.resourceData.getTradeResourceAmount(
                            resourceType,
                            selectedClass,
                        ),
                        availableFuelPerResource / selectedPrice
                    ).minOf { it }
                } else {
                    listOf(
                        desireData.desireAmount,
                        economyData.resourceData.getTradeResourceAmount(
                            resourceType,
                            selectedClass,
                        ),
                    ).minOf { it }
                }

                val totalPrice: Double = totalAmount * selectedPrice

                // Buy resource
                economyData.resourceData.getResourceAmountData(
                    resourceType,
                    selectedClass
                ).trade -= totalAmount
                commonPopData.addDesireResource(
                    resourceType,
                    selectedQuality,
                    totalAmount
                )
                commonPopData.saving -= totalPrice
                physicsData.addNewFuel(totalPrice)
            }
        }
    }
}