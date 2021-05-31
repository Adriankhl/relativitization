package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity


@Serializable
data class DummyCommand(
    override val fromId: Int = -1,
    override val toId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : Command() {
    override val name: String = "Dummy"

    override fun description(): String {
        return "Do nothing"
    }

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
    }
}
