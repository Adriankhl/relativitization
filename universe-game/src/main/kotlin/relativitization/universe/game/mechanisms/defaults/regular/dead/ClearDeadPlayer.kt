package relativitization.universe.game.mechanisms.defaults.regular.dead

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import kotlin.random.Random

/**
 * Clear data of players that are dead as observed by this player
 * Dead players are the one who are not in UniverseData3DAtPlayer
 */
object ClearDeadPlayer : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        val allPlayerId: Set<Int> = universeData3DAtPlayer.playerDataMap.keys

        // Don't clear war, since the peace treaty should be handled for dead player in UpdateWar

        // Clear diplomatic relation
        mutablePlayerData.playerInternalData.diplomacyData().relationData.relationMap.keys
            .removeAll {
                !allPlayerId.contains(it)
            }

        // Clear enemy
        mutablePlayerData.playerInternalData.diplomacyData().relationData.enemyIdSet.removeAll {
            !allPlayerId.contains(it)
        }

        // Clear ally
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys.removeAll {
            !allPlayerId.contains(it)
        }

        // Clear export tariff
        mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff
            .tariffRatePlayerMap.keys.removeAll {
                !allPlayerId.contains(it)
            }

        // Clear import tariff
        mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.importTariff
            .tariffRatePlayerMap.keys.removeAll {
                !allPlayerId.contains(it)
            }

        // Clear subordinate and direct subordinate
        val newLeaderIdList: List<Int> = mutablePlayerData.playerInternalData.leaderIdList.filter {
            allPlayerId.contains(it) && (it != mutablePlayerData.playerId)
        }
        mutablePlayerData.changeDirectLeader(newLeaderIdList)
        mutablePlayerData.playerInternalData.directSubordinateIdSet.removeAll {
            !allPlayerId.contains(it)
        }
        mutablePlayerData.playerInternalData.subordinateIdSet.removeAll {
            !allPlayerId.contains(it)
        }

        // Clear factory owned by dead players
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values
            .forEach { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.entries
                    .removeAll { (_, factory) ->
                        !allPlayerId.contains(factory.ownerPlayerId)
                    }

                carrier.allPopData.labourerPopData.resourceFactoryMap.entries
                    .removeAll { (_, factory) ->
                        !allPlayerId.contains(factory.ownerPlayerId)
                    }
            }

        // Clear modifier
        mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData.peaceTreaty
            .keys.removeAll {
                !allPlayerId.contains(it)
            }

        return listOf()
    }
}