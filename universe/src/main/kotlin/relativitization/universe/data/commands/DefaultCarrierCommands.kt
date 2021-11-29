package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

/**
 * Build a new carrier locally, can only send to self
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
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
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
        val hasFuelI18String: I18NString = I18NString("Not enough fuel")


        return CanSendCheckMessage(
            isSelf && hasFuel,
            I18NString.combine(
                listOf(
                    isSelfI18NString,
                    hasFuelI18String
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId
        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel

        return isSelf && hasFuel
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val newCarrierInternalData: MutableCarrierInternalData =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipInternalData(
                qualityLevel = qualityLevel
            )
        val newCarrier: MutableCarrierData = MutableCarrierData(
            carrierType = CarrierType.SPACESHIP,
            carrierInternalData = newCarrierInternalData
        )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        playerData.playerInternalData.popSystemData().addCarrier(newCarrier)
    }
}