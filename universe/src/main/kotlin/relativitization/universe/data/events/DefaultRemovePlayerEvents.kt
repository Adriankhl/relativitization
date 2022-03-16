package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.politicsData
import relativitization.universe.maths.random.Rand
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Ask to merge this player to its direct leader
 */
@Serializable
data class AskToMergeCarrierEvent(
    override val toId: Int,
    override val fromId: Int
) : DefaultEvent() {

    override val stayTime: Int = 1

    override fun description(): I18NString = I18NString(
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
            playerData.playerInternalData.directSubordinateIdSet.contains(toId),
            CommandI18NStringFactory.isNotDirectSubordinate(
                playerId = fromId, otherPlayerId = toId
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
            CommandI18NStringFactory.isNotDirectLeader(playerData.playerId, fromId),
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

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        // only if counter > 0, skip first turn to allow player choose
        return if (mutableEventRecordData.stayCounter > 0) {
            if (mutableEventRecordData.choice == 0) {
                val agreeMergeCommand = AgreeMergeCommand(
                    toId = toId,
                    fromId = toId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D
                )

                agreeMergeCommand.checkAndExecute(
                    mutablePlayerData,
                    universeData3DAtPlayer.universeSettings
                )

                listOf()
            } else {
                val declareIndependenceToDirectLeaderCommand = DeclareIndependenceToDirectLeaderCommand(
                    toId = fromId,
                    fromId = toId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D
                )

                declareIndependenceToDirectLeaderCommand.checkAndSelfExecuteBeforeSend(
                    mutablePlayerData,
                    universeData3DAtPlayer.universeSettings
                )

                listOf(declareIndependenceToDirectLeaderCommand)
            }
        } else {
            listOf()
        }
    }

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Int {
        return when (universeData3DAtPlayer.getCurrentPlayerData().playerType) {
            PlayerType.HUMAN -> 1
            PlayerType.NONE -> 0
            PlayerType.AI -> Rand.rand().nextInt(0, 2)
        }
    }

    override fun shouldCancelThisEvent(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        // Only cancel this event if the player agree to merge
        return universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.politicsData().hasAgreedMerge
    }
}