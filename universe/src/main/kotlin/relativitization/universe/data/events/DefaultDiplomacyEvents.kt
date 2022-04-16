package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AcceptAllianceCommand
import relativitization.universe.data.commands.AcceptPeaceCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.CommandErrorMessage
import relativitization.universe.data.components.defaults.diplomacy.ally.MutableAllianceData
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
            NormalString("Propose peace between player "),
            IntString(0),
            NormalString(" and player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            fromId.toString(),
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

        val isProposePeaceEventNotExist = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is ProposePeaceEvent
            }.values.all {
                it.event.fromId != fromId
            },
            I18NString("Propose peace event already exists. ")
        )

        return CommandErrorMessage(
            listOf(
                isInWar,
                isProposePeaceEventNotExist,
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

/**
 * Propose to become an ally
 */
@Serializable
data class ProposeAllianceEvent(
    override val toId: Int,
    override val fromId: Int,
) : DefaultEvent() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Propose alliance between player "),
            IntString(0),
            NormalString(" and player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            fromId.toString(),
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
        val isNotEnemy = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isEnemy(toId),
            I18NString("Target is enemy. ")
        )
        return CommandErrorMessage(
            listOf(
                isNotEnemy
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isNotEnemy = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isEnemy(fromId),
            I18NString("Target is enemy. ")
        )

        val isProposeAllyNotExist = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is ProposeAllianceEvent
            }.values.all {
                it.event.fromId != fromId
            },
            I18NString("Propose ally event already exists. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotEnemy,
                isProposeAllyNotExist
            )
        )
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            if (
                !mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap
                    .containsKey(fromId)
            ) {
                mutablePlayerData.playerInternalData.diplomacyData().relationData
                    .allyMap[fromId] = MutableAllianceData(mutablePlayerData.int4D.t)
                listOf(
                    AcceptAllianceCommand(
                        toId = fromId,
                        fromId = toId,
                        fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    )
                )
            } else {
                listOf()
            }
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