package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.MutablePoliticsData
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntTranslateString
import relativitization.universe.utils.NormalString

/**
 * Change the factory policy of this player
 *
 * @property allowSubordinateBuildFactory whether subordinates are allowed to build factory
 * @property allowLeaderBuildLocalFactory whether leader is allow to build local factory at subordinate
 * @property allowForeignInvestor whether foreigner are allowed to invest
 */
@Serializable
data class ChangeFactoryPolicyCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val allowSubordinateBuildFactory: Boolean = false,
    val allowLeaderBuildLocalFactory: Boolean = true,
    val allowForeignInvestor: Boolean = true,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
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
    ): CanSendCheckMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        return CanSendCheckMessage(
            isSelf && isTopLeader,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val politicsData: MutablePoliticsData = playerData.playerInternalData.politicsData()
        politicsData.allowSubordinateBuildFactory = allowSubordinateBuildFactory
        politicsData.allowLeaderBuildLocalFactory = allowLeaderBuildLocalFactory
        politicsData.allowForeignInvestor = allowForeignInvestor
    }
}