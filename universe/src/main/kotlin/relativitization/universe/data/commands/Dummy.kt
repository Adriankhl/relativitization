package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity


@Serializable
data class Dummy(
    override val fromId: Int,
    override val toId: Int,
    override val fromInt4D: Int4D,
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
