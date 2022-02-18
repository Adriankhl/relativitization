package relativitization.universe.mechanisms.defaults.regular.ai

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.aiData
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.Mechanism

object ClearRecentCommand : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        val removeTimeFactor: Int = 3

        val toRemoveSet: Set<Int> = mutablePlayerData.playerInternalData.aiData()
            .recentCommandTimeMap.filter { (otherId, time) ->
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
                val isOld: Boolean = mutablePlayerData.int4D.t - time > (maxTimeDelay * removeTimeFactor)

                noPlayer || isOld
            }.keys

        toRemoveSet.forEach {
            mutablePlayerData.playerInternalData.aiData().recentCommandTimeMap.remove(it)
        }

        return listOf()
    }
}