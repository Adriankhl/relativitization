package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AcceptPeaceCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.CommandErrorMessage
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Propose peace to a war
 */
@Serializable
data class ProposePeaceEvent(
    override val toId: Int,
    override val fromId: Int,
) : DefaultEvent() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Propose peace to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun choiceDescription(): Map<Int, I18NString> = mapOf(
        0 to I18NString("Accept"),
        1 to I18NString("Reject"),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(toId),
            I18NString("Is not in war with target. ")
        )
        return CommandErrorMessage(
            listOf(
                isInWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(fromId),
            I18NString("Is not in war with target. ")
        )
        return CommandErrorMessage(
            listOf(
                isInWar,
            )
        )
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .remove(fromId)
            listOf(
                AcceptPeaceCommand(
                    toId = fromId,
                    fromId = toId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
                )
            )
        },
        1 to {
            listOf()
        }
    )

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Int = 1

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Boolean = false

}