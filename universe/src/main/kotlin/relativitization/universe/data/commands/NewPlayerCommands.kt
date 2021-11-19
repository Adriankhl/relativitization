package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

@Serializable
data class CreateNewPlayerCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val carrierIdList: List<Int>,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Create new player with carriers: "),
            IntString(0),
        ),
        listOf(
            carrierIdList.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isToSelf: Boolean = playerData.playerId == toId
        val isToSelfI18NString: I18NString = if (isToSelf) {
            I18NString("")
        } else {
            CanSendWIthMessageI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isCarrierIdValid: Boolean = carrierIdList.all {
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(it)
        }
        val isCarrierIdValidI18String: I18NString = if (isCarrierIdValid) {
            I18NString("")
        } else {
            I18NString("Invalid carrier id")
        }

        return CanSendCheckMessage(
            isToSelf && isCarrierIdValid,
            I18NString.combine(
                listOf(
                    isToSelfI18NString,
                    isCarrierIdValidI18String,
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }

}
