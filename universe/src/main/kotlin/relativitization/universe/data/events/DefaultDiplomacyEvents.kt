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
import relativitization.universe.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.data.components.defaults.diplomacy.war.WarCoreData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.serializer.DataSerializer
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
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is ProposePeaceEvent
            }.values.all {
                it.event.fromId != fromId
            },
            I18NString("Event already exists. ")
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(fromId),
            I18NString("Is not in war with target. ")
        )

        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isInWar,
            )
        )
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Boolean {
        return !mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
            .containsKey(fromId)
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
            !playerData.playerInternalData.diplomacyData().relationData.isEnemy(toId),
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
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is ProposeAllianceEvent
            }.values.all {
                it.event.fromId != fromId
            },
            I18NString("Event already exists. ")
        )

        val isNotEnemy = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().relationData.isEnemy(fromId),
            I18NString("Target is enemy. ")
        )

        val isNotAlly = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().relationData.isAlly(fromId),
            I18NString("Target is ally. ")
        )

        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isNotEnemy,
                isNotAlly,
            )
        )
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Boolean {
        val isEnemy: Boolean = mutablePlayerData.playerInternalData.diplomacyData().relationData
            .isEnemy(fromId)

        val isAlly: Boolean = mutablePlayerData.playerInternalData.diplomacyData().relationData
            .isAlly(fromId)

        return isEnemy || isAlly
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
}

/**
 * Call an ally to join a war
 */
@Serializable
data class CallAllyToWarEvent(
    override val toId: Int,
    override val fromId: Int,
    val warTargetId: Int,
) : DefaultEvent() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" call ally player "),
            IntString(1),
            NormalString(" to join war against player "),
            IntString(2),
            NormalString(". "),
        ),
        listOf(
            fromId.toString(),
            toId.toString(),
            warTargetId.toString(),
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
        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(toId),
            I18NString("Target is not ally. ")
        )

        val hasWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(warTargetId),
            I18NString("War doesn't exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isAlly,
                hasWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.values.all {
                if (it.event is CallAllyToWarEvent) {
                    (it.event.fromId != fromId) || (it.event.warTargetId != warTargetId)
                } else {
                    true
                }
            },
            I18NString("Event already exists. ")
        )

        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(fromId),
            I18NString("Target is not ally. ")
        )

        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isAlly,
            )
        )
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Boolean {
        val isTargetDead: Boolean = !universeData3DAtPlayer.playerDataMap.containsKey(warTargetId)

        val isTargetInvalid: Boolean = mutablePlayerData.isLeaderOrSelf(warTargetId) ||
                mutablePlayerData.isSubOrdinate(warTargetId)

        val isNotAlly: Boolean = !mutablePlayerData.playerInternalData.diplomacyData().relationData
            .isAlly(fromId)

        val isAllyDead: Boolean = !universeData3DAtPlayer.playerDataMap.containsKey(fromId)

        val isWarNotExist: Boolean = if (isAllyDead) {
            false
        } else {
            !universeData3DAtPlayer.get(fromId).playerInternalData.diplomacyData().relationData
                .selfWarDataMap.containsKey(warTargetId)
        }

        return isTargetDead || isTargetInvalid || isNotAlly || isAllyDead || isWarNotExist
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            if (
                !mutablePlayerData.playerInternalData.diplomacyData().relationData
                    .hasAllyWar(fromId, warTargetId)
            ) {
                val warCoreData: WarCoreData = universeData3DAtPlayer.get(fromId).playerInternalData
                    .diplomacyData().relationData.selfWarDataMap.getValue(warTargetId).warCoreData
                mutablePlayerData.playerInternalData.diplomacyData().relationData.addAllyWar(
                    MutableWarData(DataSerializer.copy(warCoreData))
                )
            }

            listOf()
        },
        1 to {
            mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap
                .remove(fromId)
            listOf()
        }
    )

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Int = 1
}

/**
 * Call an ally to join a war of your subordinate
 */
@Serializable
data class CallAllyToSubordinateWarEvent(
    override val toId: Int,
    override val fromId: Int,
    val subordinateId: Int,
    val warTargetId: Int,
) : DefaultEvent() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" call ally player "),
            IntString(1),
            NormalString(" to join war of "),
            IntString(2),
            NormalString("against player "),
            IntString(3),
            NormalString(". "),
        ),
        listOf(
            fromId.toString(),
            toId.toString(),
            subordinateId.toString(),
            warTargetId.toString(),
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
        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(toId),
            I18NString("Target is not ally. ")
        )

        val hasWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData
                .hasSubordinateWar(subordinateId = subordinateId, opponentId = warTargetId),
            I18NString("War doesn't exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isAlly,
                hasWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.values.all {
                if (it.event is CallAllyToSubordinateWarEvent) {
                    (it.event.fromId != fromId) ||
                            (it.event.warTargetId != warTargetId) ||
                            (it.event.subordinateId != subordinateId)
                } else {
                    true
                }
            },
            I18NString("Event already exists. ")
        )

        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(fromId),
            I18NString("Target is not ally. ")
        )

        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isAlly,
            )
        )
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Boolean {
        val isTargetDead: Boolean = !universeData3DAtPlayer.playerDataMap.containsKey(warTargetId)

        val isTargetInvalid: Boolean = mutablePlayerData.isLeaderOrSelf(warTargetId) ||
                mutablePlayerData.isSubOrdinate(warTargetId)

        val isNotAlly: Boolean = !mutablePlayerData.playerInternalData.diplomacyData().relationData
            .isAlly(fromId)

        val isAllyDead: Boolean = !universeData3DAtPlayer.playerDataMap.containsKey(fromId)

        val isWarNotExist: Boolean = if (isAllyDead) {
            false
        } else {
            !universeData3DAtPlayer.get(fromId).playerInternalData.diplomacyData().relationData
                .hasSubordinateWar(subordinateId = subordinateId, opponentId = warTargetId)
        }

        return isTargetDead || isTargetInvalid || isNotAlly || isAllyDead || isWarNotExist
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            if (
                !mutablePlayerData.playerInternalData.diplomacyData().relationData
                    .hasAllySubordinateWar(
                        allyId = fromId,
                        allySubordinateId = subordinateId,
                        opponentId = warTargetId
                    )
            ) {
                val warCoreData: WarCoreData = universeData3DAtPlayer.get(fromId).playerInternalData
                    .diplomacyData().relationData.subordinateWarDataMap.getValue(subordinateId)
                    .getValue(warTargetId).warCoreData

                mutablePlayerData.playerInternalData.diplomacyData().relationData
                    .addAllySubordinateWar(
                        fromId,
                        MutableWarData(DataSerializer.copy(warCoreData))
                    )
            }

            listOf()
        },
        1 to {
            mutablePlayerData.playerInternalData.diplomacyData().relationData.allyMap
                .remove(fromId)
            listOf()
        }
    )

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
    ): Int = 1
}