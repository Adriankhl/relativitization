package relativitization.universe.game.mechanisms.defaults.regular.ai

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.aiData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.mechanisms.Mechanism
import kotlin.random.Random

object ClearRecentCommand : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // Parameters
        val removeTimeFactor = 3

        mutablePlayerData.playerInternalData.aiData().recentCommandTimeMap.entries
            .removeAll { (otherId, time) ->
                // Remove if player does not exist
                val noPlayer: Boolean = !universeData3DAtPlayer.playerDataMap.containsKey(otherId)

                // Remove if the time was too far, compare to the max time delay in the universe
                val maxTimeDelay: Int = Intervals.intDelay(
                    Int3D(0, 0, 0),
                    Int3D(
                        universeSettings.xDim - 1,
                        universeSettings.yDim - 1,
                        universeSettings.zDim - 1,
                    ),
                    universeSettings.speedOfLight
                )
                val isOld: Boolean =
                    mutablePlayerData.int4D.t - time > (maxTimeDelay * removeTimeFactor)

                noPlayer || isOld
            }

        return listOf()
    }
}