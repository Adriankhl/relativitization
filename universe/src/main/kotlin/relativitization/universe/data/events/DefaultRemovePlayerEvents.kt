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

    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Merge all your carriers to player "),
            IntString(0)
        ),
        listOf(
            fromId.toString()
        )
    )

    override fun choiceDescription(): Map<Int, I18NString> = mapOf(
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
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            mutablePlayerData.playerInternalData.politicsData().hasAgreedMerge = true
            listOf()
        },
        1 to {
            listOf()
        }
    )

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Int {
        return when (universeData3DAtPlayer.getCurrentPlayerData().playerType) {
            PlayerType.HUMAN -> 1
            PlayerType.NONE -> 0
            PlayerType.AI -> Rand.rand().nextInt(0, 2)
        }
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Boolean {
        // Only cancel this event if the player agree to merge
        return universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.politicsData()
            .hasAgreedMerge
    }
}