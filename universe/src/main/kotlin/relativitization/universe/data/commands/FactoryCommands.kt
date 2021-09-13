package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.utils.I18NString

@Serializable
data class BuildFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val topLeaderId: Int,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(),
        listOf()
    )

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.topLeaderId() == topLeaderId
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val sameTopLeader: Boolean = playerData.topLeaderId() == topLeaderId
        return sameTopLeader || playerData.playerInternalData.politicsData().allowForeignInvestor
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }
}