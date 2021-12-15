package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import kotlin.random.Random

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
            NormalString("Merge all your carriers to player "),
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

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectSubordinate = CommandErrorMessage(
            playerData.playerInternalData.directSubordinateIdList.contains(toId),
            CommandI18NStringFactory.isNotDirectSubordinate(
                playerId = fromId, toId = toId
            )
        )

        return CommandErrorMessage(
            listOf(
                isDirectSubordinate
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectLeader = CommandErrorMessage(
            playerData.playerInternalData.directLeaderId == fromId,
            I18NString("Sender is not direct leader. ")
        )

        val isNotTopLeader = CommandErrorMessage(
            !playerData.isTopLeader(),
            CommandI18NStringFactory.isTopLeader(playerData.playerId)
        )

        return CommandErrorMessage(
            listOf(
                isDirectLeader,
                isNotTopLeader,
            )
        )
    }

    override fun generateCommands(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        // only if counter > 0, skip first turn to allow player choose
        return if ((mutableEventRecordData.stayCounter > 0) && (mutableEventRecordData.choice == 0)) {
            listOf(
                AgreeMergeCommand(
                    toId = toId,
                    fromId = toId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D
                )
            )
        } else {
            listOf(
                DeclareIndependenceCommand(
                    toId = fromId,
                    fromId = toId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D
                )
            )
        }
    }

    override fun defaultChoice(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Int {
        return when (universeData3DAtPlayer.getCurrentPlayerData().playerType) {
            PlayerType.HUMAN -> 1
            PlayerType.NONE -> 0
            PlayerType.AI -> Random.nextInt(0, 2)
        }
    }

    override fun shouldCancelThisEvent(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        // Only cancel this event if the player agree to merge
        return universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.politicsData().agreeMerge
    }
}