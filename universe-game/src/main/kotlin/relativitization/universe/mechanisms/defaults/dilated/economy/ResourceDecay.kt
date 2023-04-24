package relativitization.universe.mechanisms.defaults.dilated.economy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.economyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

/**
 * Stored resource reduce as time toes
 */
object ResourceDecay : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // Parameters
        val decayFactor = 0.99

        ResourceType.values().forEach { resourceType ->
            ResourceQualityClass.values().forEach { resourceQualityClass ->
                mutablePlayerData.playerInternalData.economyData().resourceData
                    .getSingleResourceData(
                        resourceType,
                        resourceQualityClass
                    ).resourceAmount.storage *= decayFactor

                mutablePlayerData.playerInternalData.economyData().resourceData
                    .getSingleResourceData(
                        resourceType,
                        resourceQualityClass
                    ).resourceAmount.production *= decayFactor

                mutablePlayerData.playerInternalData.economyData().resourceData
                    .getSingleResourceData(
                        resourceType,
                        resourceQualityClass
                    ).resourceAmount.trade *= decayFactor
            }
        }

        return listOf()
    }
}