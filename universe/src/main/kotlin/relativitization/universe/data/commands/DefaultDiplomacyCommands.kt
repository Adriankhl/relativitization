package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.modifierData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Declare war on target player
 */
@Serializable
data class DeclareWarCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Declare war on "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isNotLeaderOrSelf = CommandErrorMessage(
            !playerData.isLeaderOrSelf(toId),
            I18NString("Target is leader. ")
        )

        val isNotSubordinateOrSelf = CommandErrorMessage(
            !playerData.isSubOrdinateOrSelf(toId),
            I18NString("Target is subordinate. ")
        )

        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeaceTreaty = CommandErrorMessage(
            !playerData.playerInternalData.modifierData().diplomacyModifierData.hasPeaceTreaty(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
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
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).startTime = playerData.int4D.t

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).isOffensive = true

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).warTargetTopLeaderId = toId
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        // Not already in war
        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(fromId),
            I18NString("Target is in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotInWar,
            )
        )
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
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).startTime = playerData.int4D.t

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).isOffensive = false

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).warTargetTopLeaderId = fromId
    }
}

/**
 * Declare independence war on direct leader
 */
@Serializable
data class DeclareIndependenceToDirectLeaderCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Declare independence and war on direct leader: id "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectLeader = CommandErrorMessage(
            playerData.playerInternalData.directLeaderId == toId,
            CommandI18NStringFactory.isNotDirectLeader(playerData.playerId, toId),
        )

        val isNotSelf = CommandErrorMessage(
            playerData.playerId != toId,

            I18NString("Cannot declare war on self. ")
        )

        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeaceTreaty = CommandErrorMessage(
            !playerData.playerInternalData.modifierData().diplomacyModifierData.hasPeaceTreaty(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isDirectLeader,
                isNotSelf,
                isNotInWar,
                isNotInPeaceTreaty,
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
        playerData.changeDirectLeader(newLeaderIdList)

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            toId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).startTime = playerData.int4D.t

        // Auto propose peace
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).proposePeace = true

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).isOffensive = true

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).warTargetTopLeaderId = toId
    }


    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        // Not already in war
        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(fromId),
            I18NString("Target is in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotInWar
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        // Remove subordinate
        playerData.removeSubordinateId(fromId)

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            fromId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).startTime = playerData.int4D.t

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).isOffensive = false

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).warTargetTopLeaderId = fromId
    }
}

/**
 * Declare independence war on direct leader
 */
@Serializable
data class DeclareIndependenceToTopLeaderCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Declare independence and war on top leader: id "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isTopLeader = CommandErrorMessage(
            playerData.topLeaderId() == toId,
            CommandI18NStringFactory.isTopLeaderIdWrong(toId, playerData.topLeaderId()),
        )

        val isNotSelf = CommandErrorMessage(
            playerData.playerId != toId,
            I18NString("Cannot declare war on self. ")
        )

        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeaceTreaty = CommandErrorMessage(
            !playerData.playerInternalData.modifierData().diplomacyModifierData.hasPeaceTreaty(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isTopLeader,
                isNotSelf,
                isNotInWar,
                isNotInPeaceTreaty,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        // Change direct leader and leader id list
        playerData.changeDirectLeader(
            listOf()
        )

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            toId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).startTime = playerData.int4D.t

        // Auto propose peace
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).proposePeace = true

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).isOffensive = true

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            toId
        ).warTargetTopLeaderId = toId
    }


    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        // Not already in war
        val isNotInWar = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(fromId),
            I18NString("Target is in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotInWar
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        // Remove subordinate
        playerData.removeSubordinateId(fromId)

        // Change diplomatic relation state
        playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
            fromId
        ).diplomaticRelationState = DiplomaticRelationState.ENEMY

        // Add war state
        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).initialSubordinateSet = playerData.playerInternalData.subordinateIdSet.toMutableSet()

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).startTime = playerData.int4D.t

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).isOffensive = false

        playerData.playerInternalData.diplomacyData().warData.getWarStateData(
            fromId
        ).warTargetTopLeaderId = fromId
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
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Propose peace with "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            targetPlayerId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            I18NString("Is not sending to self. ")
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                targetPlayerId
            ),
            I18NString("Is not in war with target. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(targetPlayerId),
            I18NString("Target is not in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.diplomacyData().warData.warStateMap.getValue(
            targetPlayerId
        ).proposePeace = true
    }
}

/**
 * Surrender to become a direct subordinate
 * Send to the player himself instead of target player
 *
 * @property targetPlayerId the target player which is in war
 */
@Serializable
data class SurrenderCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetPlayerId: Int,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Surrender and become subordinate of "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            targetPlayerId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            I18NString("Is not sending to self. ")
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                targetPlayerId
            ),
            I18NString("Is not in war with target. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(targetPlayerId),
            I18NString("Target is not in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.changeDirectLeader(listOf(targetPlayerId))
    }
}