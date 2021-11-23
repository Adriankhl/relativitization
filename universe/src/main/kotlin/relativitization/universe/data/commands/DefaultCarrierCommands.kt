package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.default.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

/**
 * Build a new carrier locally
 */
@Serializable
data class BuildLocalCarrierCommands(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val qualityLevel: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a new carrier with quality level "),
            IntString(0),
        ),
        listOf(
            qualityLevel.toString()
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isSelf: Boolean = playerData.playerId == fromId
        val isSelfI18String: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CanSendCheckMessageI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel


        return CanSendCheckMessage(
            isSelf,
            I18NString.combine(
                listOf(
                    isSelfI18String
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }
}