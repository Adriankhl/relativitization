package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int4D

@Serializable
data class MoveToPlayerEvent(
    override val playerId: Int,
    val targetPlayerId: Int,
    override val stayTime: Int,
) : Event() {
    override val name: String = "Move to player"

    override val description: String = "Player $playerId moving to $targetPlayerId"


    override fun canSend(playerData: PlayerData, toId: Int, universeSettings: UniverseSettings): Boolean {
        return playerData.isSubOrdinateOrSelf(toId)
    }

    override fun canExecute(playerData: MutablePlayerData, fromId: Int, universeSettings: UniverseSettings): Boolean {
        return playerData.isLeaderOrSelf(fromId)
    }

    override val choiceDescription: Map<Int, String> = mapOf(
        0 to "Moving to player $targetPlayerId",
        1 to "Cancel this command"
    )

    override fun generateCommands(choice: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        TODO("Not yet implemented")
    }


    override fun defaultChoice(universeData3DAtPlayer: UniverseData3DAtPlayer): Int {
        return 0
    }

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        return if (mutableEventData.choice == 1) {
            true
        } else {
            val selfInt4D: Int4D = universeData3DAtPlayer.get(playerId).int4D
            val selfGroupId: Int = universeData3DAtPlayer.get(playerId).groupId
            val targetInt4D: Int4D = universeData3DAtPlayer.get(targetPlayerId).int4D
            val targetGroupId: Int = universeData3DAtPlayer.get(targetPlayerId).groupId

            (selfInt4D == targetInt4D) && (selfGroupId == targetGroupId)
        }
    }
}