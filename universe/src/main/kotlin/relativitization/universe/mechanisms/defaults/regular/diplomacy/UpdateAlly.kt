package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.diplomacy.ally.MutableAllianceData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.Mechanism

object UpdateAlly : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val allAlly: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap.keys

        // Remove all dead player, enemy, and broken alliance
        val allyToKeep: Set<Int> = allAlly.filter {
            universeData3DAtPlayer.playerDataMap.containsKey(it)
        }.filter {
            !mutablePlayerData.playerInternalData.diplomacyData().relationData.isEnemy(it)
        }.filter {
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
                otherPlayerData.playerInternalData.diplomacyData().relationData.isAlly(
                    mutablePlayerData.playerId
                )
            } else {
                true
            }
        }.toSet()

        val allyToRemove: Set<Int> = allAlly - allyToKeep

        allyToRemove.forEach {
            mutablePlayerData.playerInternalData.diplomacyData().relationData.relationMap.remove(it)
        }

        return listOf()
    }
}