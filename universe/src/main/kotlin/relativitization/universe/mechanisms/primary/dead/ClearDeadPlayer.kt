package relativitization.universe.mechanisms.primary.dead

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Clear data of players that are dead as observed by this player
 * Dead players are the one who are not in UniverseData3DAtPlayer
 */
object ClearDeadPlayer : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val allPlayerId: Set<Int> = universeData3DAtPlayer.playerDataMap.keys

        // Clear diplomatic relation
        val toRemoveRelationKeys: List<Int> = mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys.filter {
            !allPlayerId.contains(it)
        }
        toRemoveRelationKeys.forEach {
            mutablePlayerData.playerInternalData.diplomacyData().relationMap.remove(it)
        }

        // Clear war
        val toRemoveWarKeys: List<Int> = mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.keys.filter {
            !allPlayerId.contains(it)
        }
        toRemoveWarKeys.forEach {
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(it)
        }

        // Clear export tariff
        val toRemoveExportTariffKeys: List<Int> = mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.tariffRatePlayerMap.keys.filter {
            !allPlayerId.contains(it)
        }
        toRemoveExportTariffKeys.forEach {
            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.tariffRatePlayerMap.remove(it)
        }

        // Clear import tariff
        val toRemoveImportTariffKeys: List<Int> = mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.importTariff.tariffRatePlayerMap.keys.filter {
            !allPlayerId.contains(it)
        }
        toRemoveImportTariffKeys.forEach {
            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.importTariff.tariffRatePlayerMap.remove(it)
        }



        return listOf()
    }
}