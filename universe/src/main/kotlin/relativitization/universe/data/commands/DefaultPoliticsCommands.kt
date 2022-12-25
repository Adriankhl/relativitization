package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.MutablePoliticsData
import relativitization.universe.data.components.politicsData
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntTranslateString
import relativitization.universe.utils.NormalString

/**
 * Change the factory policy of this player
 *
 * @property allowSubordinateBuildFactory whether subordinates are allowed to build factory
 * @property allowLeaderBuildLocalFactory whether leader is allowed to build local factory
 *  at subordinate
 * @property allowForeignInvestor whether foreigner are allowed to invest
 */
@Serializable
data class ChangeFactoryPolicyCommand(
    override val toId: Int,
    val allowSubordinateBuildFactory: Boolean = false,
    val allowLeaderBuildLocalFactory: Boolean = true,
    val allowForeignInvestor: Boolean = true,
) : DefaultCommand() {
    override fun name(): String = "Change Factory Policy"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Change the factory policy: allow subordinate build factory ("),
            IntTranslateString(0),
            NormalString("), allow leader build local factory ("),
            IntTranslateString(1),
            NormalString("), allow foreign investor ("),
            IntTranslateString(2),
            NormalString("). ")
        ),
        listOf(
            allowForeignInvestor.toString(),
            allowLeaderBuildLocalFactory.toString(),
            allowForeignInvestor.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
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

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val politicsData: MutablePoliticsData = playerData.playerInternalData.politicsData()
        politicsData.isSubordinateBuildFactoryAllowed = allowSubordinateBuildFactory
        politicsData.isLeaderBuildLocalFactoryAllowed = allowLeaderBuildLocalFactory
        politicsData.isForeignInvestorAllowed = allowForeignInvestor
    }
}