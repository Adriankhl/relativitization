package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AgreeMergeCommand
import relativitization.universe.data.commands.CanSendCheckMessage
import relativitization.universe.data.commands.CanSendCheckMessageI18NStringFactory
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

/**
 * Ask to merge this player to its direct leader
 */
@Serializable
data class AskToMergeCarrierEvent(
    override val toId: Int,
    override val fromId: Int
) : DefaultEvent() {

    override val stayTime: Int = 1

    override val description: I18NString = I18NString(
        listOf(
            RealString("Merge all your carriers to player "),
            IntString(0)
        ),
        listOf(
            fromId.toString()
        )
    )

    override val choiceDescription: Map<Int, I18NString> = mapOf(
        0 to I18NString("Accept. Warning! You are going to die"),
        1 to I18NString("Reject")
    )

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        // Only cancel this event if the player agree to merge
        return universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.politicsData().agreeMerge
    }

    override fun defaultChoice(eventId: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): Int {
        return when (universeData3DAtPlayer.getCurrentPlayerData().playerType) {
            PlayerType.HUMAN -> 1
            PlayerType.NONE -> 0
            PlayerType.AI -> 1
        }
    }

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isDirectSubordinate: Boolean =
            playerData.playerInternalData.directSubordinateIdList.contains(toId)
        val isDirectSubordinateI18NString: I18NString =
            CanSendCheckMessageI18NStringFactory.isNotDirectSubordinate(
                playerId = fromId, toId = toId
            )

        return CanSendCheckMessage(
            isDirectSubordinate,
            I18NString.combine(
                listOf(
                    isDirectSubordinateI18NString
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerInternalData.directLeaderId == fromId) && !playerData.isTopLeader()
    }

    override fun generateCommands(
        eventId: Int,
        choice: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        return if (choice == 0) {
            listOf(
                AgreeMergeCommand(
                    toId = toId,
                    fromId = toId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D
                )
            )
        } else {
            listOf()
        }
    }
}