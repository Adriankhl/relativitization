package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

@Serializable
data class DeclareWarCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Declare war on "),
            IntString(0),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isNotLeader: Boolean = !playerData.isLeaderOrSelf(toId)
        val isNotLeaderI18NString: I18NString = if (isNotLeader) {
            I18NString("")
        } else {
            I18NString("Target is leader. ")
        }

        val isNotSubordinate: Boolean = !playerData.isLeaderOrSelf(toId)
        val isNotSubordinateI18NString: I18NString = if (isNotSubordinate) {
            I18NString("")
        } else {
            I18NString("Target is leader. ")
        }

        val isNotInWar: Boolean =
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId)
        val isNotInWarI18NString: I18NString = if (isNotInWar) {
            I18NString("")
        } else {
            I18NString("Target is in war with you. ")
        }

        val isNotInPeaceTreaty: Boolean =
            !playerData.playerInternalData.modifierData().diplomacyModifierData.canDeclareWar(toId)
        val isNotInPeaceTreatyI18NString: I18NString = if (isNotInPeaceTreaty) {
            I18NString("")
        } else {
            I18NString("Target is in peace with you. ")
        }

        return CanSendCheckMessage(
            isNotLeader && isNotSubordinate && isNotInWar && isNotInPeaceTreaty,
            listOf(
                isNotLeaderI18NString,
                isNotSubordinateI18NString,
                isNotInWarI18NString,
                isNotInPeaceTreatyI18NString,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            toId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).initialSubordinateList = playerData.playerInternalData.subordinateIdList

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).startTime = playerData.int4D.t
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        // Not already in war
        return !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(fromId)
    }

    override fun execute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings,
    ) {
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            fromId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).initialSubordinateList = playerData.playerInternalData.subordinateIdList

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).startTime = playerData.int4D.t
    }
}

/**
 * Declare independence war on direct leader
 */
@Serializable
data class DeclareIndependenceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Declare independence and war on "),
            IntString(0),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isDirectLeader: Boolean = playerData.playerInternalData.directLeaderId == toId
        val isDirectLeaderI18NString: I18NString = if (isDirectLeader) {
            I18NString("")
        } else {
            I18NString("Target is not direct leader. ")
        }

        val isNotSelf: Boolean = playerData.playerId != toId
        val isNotSelfI18NString: I18NString = if (isNotSelf) {
            I18NString("")
        } else {
            I18NString("Cannot declare war on self. ")
        }

        val isNotInWar: Boolean =
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId)
        val isNotInWarI18NString: I18NString = if (isNotInWar) {
            I18NString("")
        } else {
            I18NString("Target is in war with you. ")
        }

        return CanSendCheckMessage(
            isDirectLeader && isNotSelf && isNotInWar,
            listOf(
                isDirectLeaderI18NString,
                isNotSelfI18NString,
                isNotInWarI18NString,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        // Change direct leader and leader id list
        val newLeaderIdList: List<Int> = playerData.playerInternalData.leaderIdList.filter {
            (it != playerData.playerId) && (it != toId)
        }
        playerData.changeDirectLeaderId(
            newLeaderIdList
        )

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            toId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).initialSubordinateList = playerData.playerInternalData.subordinateIdList

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).startTime = playerData.int4D.t
    }


    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        // Not already in war
        return !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        // Remove subordinate
        playerData.removeSubordinate(fromId)

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            fromId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).initialSubordinateList = playerData.playerInternalData.subordinateIdList

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).startTime = playerData.int4D.t
    }
}

/**
 * Change player war state to propose peace
 * Send to the player himself instead of target player
 *
 * @property targetPlayerId the target player which is in war
 */
@Serializable
data class ProposePeaceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetPlayerId: Int,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Propose peace with "),
            IntString(0),
        ),
        listOf(
            targetPlayerId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            I18NString("Is not sending to self. ")
        }

        return CanSendCheckMessage(
            isSelf,
            listOf(
                isSelfI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
            targetPlayerId
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.diplomacyData().warData.warStateMap.getValue(
            targetPlayerId
        ).proposePeace = true
    }
}