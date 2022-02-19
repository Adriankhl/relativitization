package relativitization.universe.mechanisms.defaults.regular.sync

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.syncData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Sync data component before all mechanism start
 */
object SyncPlayerData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.syncData()

        return listOf()
    }
}