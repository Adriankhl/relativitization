package relativitization.universe.mechanisms.defaults.regular.storage

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.*
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object BalanceResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val resourceData: MutableResourceData = mutablePlayerData.playerInternalData.economyData().resourceData
        ResourceType.values().forEach { resourceType ->
            ResourceQualityClass.values().forEach { resourceQualityClass ->
                val target: MutableResourceTargetProportionData = resourceData.getResourceTargetProportionData(
                    resourceType, resourceQualityClass
                )
                val resourceAmountData: MutableResourceAmountData = resourceData.getResourceAmountData(
                    resourceType, resourceQualityClass
                )

                val totalWeight: Double = target.total()

                val targetStorage: Double = if (totalWeight > 0.0) {
                    target.storage / totalWeight * resourceAmountData.total()
                } else {
                    resourceAmountData.total() / 3.0
                }

                if (resourceAmountData.storage > targetStorage) {
                    val redistributeAmount: Double = resourceAmountData.storage - targetStorage
                    resourceAmountData.storage = targetStorage
                    resourceData.addResource(
                        newResourceType = resourceType,
                        newResourceQuality = resourceData.getResourceQuality(resourceType, resourceQualityClass),
                        newResourceAmount = redistributeAmount
                    )
                }
            }
        }
        val target: MutableFuelRestMassTargetProportionData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassTargetProportionData
        val fuelData: MutableFuelRestMassData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassData

        val totalWeight: Double = target.total()




        return listOf()
    }
}