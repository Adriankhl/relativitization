package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.game.data.components.defaults.diplomacy.ally.MutableAllianceData
import relativitization.universe.game.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.game.data.components.defaults.diplomacy.war.WarCoreData
import relativitization.universe.game.data.components.defaults.diplomacy.war.WarReason
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString
import relativitization.universe.game.data.components.defaults.diplomacy.isAlly

/**
 * Declare war on target player
 */
@Serializable
data class DeclareWarCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Declare War"

    override fun description(fromId: Int): I18NString = I18NString(
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
            !playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(toId),
            I18NString("Target is in war with you. ")
        )


        val isNotInPeace = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().peacePlayerIdSet.contains(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotLeaderOrSelf,
                isNotSubordinateOrSelf,
                isNotInWar,
                isNotInPeace,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = playerData.playerId,
                opponentId = toId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INVASION,
                isOffensive = true,
                isDefensive = false,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings,
    ) {
        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = toId,
                opponentId = fromId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INVASION,
                isOffensive = false,
                isDefensive = true,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
    }
}

/**
 * Declare independence war on direct leader
 */
@Serializable
data class DeclareIndependenceToDirectLeaderCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Declare Independence (Direct)"
    override fun description(fromId: Int): I18NString = I18NString(
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
            !playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeace = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().peacePlayerIdSet.contains(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isDirectLeader,
                isNotSelf,
                isNotInWar,
                isNotInPeace,
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

        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = playerData.playerId,
                opponentId = toId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INDEPENDENCE,
                isOffensive = true,
                isDefensive = false,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
    }


    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        // Remove subordinate
        playerData.removeSubordinateId(fromId)

        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = toId,
                opponentId = fromId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INDEPENDENCE,
                isOffensive = false,
                isDefensive = true,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
    }
}

/**
 * Declare independence war on direct leader
 */
@Serializable
data class DeclareIndependenceToTopLeaderCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Declare Independence (Top)"

    override fun description(fromId: Int): I18NString = I18NString(
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
            !playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(toId),
            I18NString("Target is in war with you. ")
        )

        val isNotInPeace = CommandErrorMessage(
            !playerData.playerInternalData.diplomacyData().peacePlayerIdSet.contains(toId),
            I18NString("Target is in peace with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isTopLeader,
                isNotSelf,
                isNotInWar,
                isNotInPeace,
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

        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = playerData.playerId,
                opponentId = toId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INDEPENDENCE,
                isOffensive = true,
                isDefensive = false,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
    }


    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        // Remove subordinate
        playerData.removeSubordinateId(fromId)


        val warData = MutableWarData(
            warCoreData = WarCoreData(
                supportId = toId,
                opponentId = fromId,
                startTime = playerData.int4D.t,
                warReason = WarReason.INDEPENDENCE,
                isOffensive = false,
                isDefensive = true,
            ),
        )

        playerData.playerInternalData.diplomacyData().relationData.addSelfWar(warData)
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
    val targetPlayerId: Int,
) : DefaultCommand() {
    override fun name(): String = "Surrender"

    override fun description(fromId: Int): I18NString = I18NString(
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
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(targetPlayerId),
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
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val isInWar = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .containsKey(targetPlayerId),
            I18NString("Target is not in war with you. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isInWar,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.changeDirectLeader(listOf(targetPlayerId))
    }
}

/**
 * Accept peace to remove war
 */
@Serializable
data class AcceptPeaceCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Accept Peace"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap.remove(fromId)
    }
}

/**
 * Accept alliance
 */
@Serializable
data class AcceptAllianceCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Accept Alliance"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.diplomacyData().relationData.allyMap[fromId] =
            MutableAllianceData(playerData.int4D.t)
    }
}

/**
 * Remove an ally
 * Send to the player himself instead of target player
 * The ally should automatically remove this player from ally after seeing this
 *
 * @property targetPlayerId the target player which is in war
 */
@Serializable
data class RemoveAllyCommand(
    override val toId: Int,
    val targetPlayerId: Int,
) : DefaultCommand() {
    override fun name(): String = "Remove Ally"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Break alliance with player "),
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

        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(targetPlayerId),
            I18NString("Is not an ally. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isAlly,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val isAlly = CommandErrorMessage(
            playerData.playerInternalData.diplomacyData().relationData.isAlly(targetPlayerId),
            I18NString("Is not an ally. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isAlly,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.diplomacyData().relationData.allyMap.remove(targetPlayerId)
    }
}