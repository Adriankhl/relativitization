package relativitization.universe.game.mechanisms.defaults.dilated.economy

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.mechanisms.Mechanism
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