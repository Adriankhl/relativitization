package relativitization.universe.mechanisms.primary.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableEconomyData
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.economy.MutableResourceQualityData
import relativitization.universe.data.components.economy.ResourceQualityClass
import relativitization.universe.data.components.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
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

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            PopType.values().forEach { popType ->
                val commonPopData = mutableCarrierData.allPopData.getCommonPopData(popType)
                buyFromThisPlayer(
                    gamma,
                    mutablePlayerData.playerInternalData.physicsData(),
                    commonPopData,
                    mutablePlayerData.playerInternalData.economyData(),
                )
            }
        }

        return listOf()
    }

    fun buyFromThisPlayer(
        gamma: Double,
        physicsData: MutablePhysicsData,
        commonPopData: MutableCommonPopData,
        economyData: MutableEconomyData,
    ) {
        val numDesire: Double = commonPopData.desireResourceMap.size.toDouble()

        val availableFuelPerResource: Double = commonPopData.saving / numDesire


        commonPopData.desireResourceMap.forEach { resourceType, desireData ->
            val selectedClass: ResourceQualityClass =
                ResourceQualityClass.values().firstOrNull { resourceQualityClass ->
                    val amount: Double = economyData.resourceData.getTradeResourceAmount(
                        resourceType = resourceType,
                        resourceQualityClass = resourceQualityClass
                    )

                    val price: Double = economyData.resourceData.getResourcePrice(
                        resourceType = resourceType,
                        resourceQualityClass = resourceQualityClass
                    )

                    // Pick the resource class with sufficient amount and sufficiently cheap
                    // Adjusted by time dilation
                    (amount >= desireData.desireAmount / gamma) && (availableFuelPerResource >= price * desireData.desireAmount / gamma)
                } ?: ResourceQualityClass.THIRD

            val selectedQuality: MutableResourceQualityData = economyData.resourceData.getResourceQuality(
                resourceType = resourceType,
                resourceQualityClass = selectedClass,
            )

            val selectedPrice: Double = economyData.resourceData.getResourcePrice(
                resourceType = resourceType,
                resourceQualityClass = selectedClass
            )

            val totalAmount: Double = if (selectedPrice > 0.0) {
                listOf(
                    desireData.desireAmount / gamma,
                    economyData.resourceData.getTradeResourceAmount(
                        resourceType,
                        selectedClass,
                    ),
                    availableFuelPerResource / selectedPrice
                ).minOf { it }
            } else {
                listOf(
                    desireData.desireAmount / gamma,
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
            physicsData.addFuel(totalPrice)
        }
    }
}