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
    ): CommandSuccessMessage {
        val isNotLeaderOrSelf = CommandSuccessMessage(
            !playerData.isLeaderOrSelf(toId),
            I18NString("Target is leader. ")
        )

        val isNotSubordinateOrSelf = CommandSuccessMessage(
            !playerData.isSubOrdinateOrSelf(toId),
            I18NString("Target is subordinate. ")
        )

        val isNotInWar = CommandSuccessMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeaceTreaty = CommandSuccessMessage(
            !playerData.playerInternalData.modifierData().diplomacyModifierData.canDeclareWar(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandSuccessMessage(
            listOf(
                isNotLeaderOrSelf,
                isNotSubordinateOrSelf,
                isNotInWar,
                isNotInPeaceTreaty,
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
    ): CommandSuccessMessage {
        val isDirectLeader = CommandSuccessMessage(
            playerData.playerInternalData.directLeaderId == toId,

            I18NString("Target is not direct leader. ")
        )

        val isNotSelf = CommandSuccessMessage(
            playerData.playerId != toId,

            I18NString("Cannot declare war on self. ")
        )

        val isNotInWar = CommandSuccessMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        return CommandSuccessMessage(
            listOf(
                isDirectLeader,
                isNotSelf,
                isNotInWar
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
    ): CommandSuccessMessage {
        val isSelf = CommandSuccessMessage(
            playerData.playerId == toId,
            I18NString("Is not sending to self. ")
        )

        val isInWar = CommandSuccessMessage(
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                targetPlayerId
            ),
            I18NString("Is not in war with target. ")
        )

        return CommandSuccessMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId

        val isInWar: Boolean =
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                targetPlayerId
            )

        return isSelf && isInWar
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.diplomacyData().warData.warStateMap.getValue(
            targetPlayerId
        ).proposePeace = true
    }
}