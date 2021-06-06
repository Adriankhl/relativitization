package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command

@Serializable
data class MoveToPlayerEvent(
    override val playerId: Int,
    val targetPlayerId: Int,
) : Event() {
    override val name: String = "Move to player"

    override val description: String = "Player $playerId moving to $targetPlayerId"

    override val choiceDescription: Map<Int, String>
        get() = TODO("Not yet implemented")

    override val stayTime: Int
        get() = TODO("Not yet implemented")

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateCommands(choice: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        TODO("Not yet implemented")
    }

    override fun defaultChoice(universeData3DAtPlayer: UniverseData3DAtPlayer): Int {
        TODO("Not yet implemented")
    }

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        TODO("Not yet implemented")
    }
}