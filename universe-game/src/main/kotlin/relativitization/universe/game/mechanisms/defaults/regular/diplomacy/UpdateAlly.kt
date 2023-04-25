package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.PlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.defaults.diplomacy.ally.MutableAllianceData
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.maths.physics.Int4D
import relativitization.universe.game.maths.physics.Intervals
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object UpdateAlly : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // Remove all dead player, leader or subordinate, enemy, and broken alliance
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys.removeAll {
            !universeData3DAtPlayer.playerDataMap.containsKey(it)
        }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys.removeAll {
            mutablePlayerData.playerInternalData.diplomacyData().relationData.isEnemy(it)
        }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys.removeAll {
            mutablePlayerData.isSubOrdinateOrSelf(it) || mutablePlayerData.isLeader(it)
        }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys.removeAll {
            val otherPlayerData: PlayerData = universeData3DAtPlayer.get(it)
            val otherInt4D: Int4D = otherPlayerData.int4D
            val timeDelay: Int = Intervals.intDelay(
                c1 = mutablePlayerData.int4D.toInt3D(),
                c2 = otherInt4D.toInt3D(),
                speedOfLight = universeSettings.speedOfLight
            )

            val allianceData: MutableAllianceData = mutablePlayerData.playerInternalData
                .diplomacyData().relationData.allyMap.getValue(it)

            if ((mutablePlayerData.int4D.t - allianceData.startTime) >= 2 * timeDelay) {
                !otherPlayerData.playerInternalData.diplomacyData().relationData.isAlly(
                    mutablePlayerData.playerId
                )
            } else {
                false
            }
        }

        return listOf()
    }
}