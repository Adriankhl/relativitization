package relativitization.universe.game.mechanisms.defaults.regular.storage

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.economy.MutableResourceAmountData
import relativitization.universe.game.data.components.defaults.economy.MutableResourceData
import relativitization.universe.game.data.components.defaults.economy.MutableResourceTargetProportionData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getResourceAmountData
import relativitization.universe.game.data.components.defaults.economy.getResourceQuality
import relativitization.universe.game.data.components.defaults.economy.getResourceTargetProportionData
import relativitization.universe.game.data.components.defaults.economy.total
import relativitization.universe.game.data.components.economyData
import kotlin.random.Random

object BalanceResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
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

        return listOf()
    }
}