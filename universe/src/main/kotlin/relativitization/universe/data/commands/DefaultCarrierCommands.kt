package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Build a new carrier locally, can only send to self
 */
@Serializable
data class BuildLocalCarrierCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val qualityLevel: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Build a new carrier with quality level "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            qualityLevel.toString()
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel,
            I18NString("Not enough fuel. ")
        )


        return CommandErrorMessage(
            listOf(
                isSelf,
                hasFuel
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, toId)
        )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel,
            I18NString("Not enough fuel. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasFuel
            )
        )
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

        playerData.playerInternalData.physicsData().removeExternalProductionFuel(requiredFuel)

        playerData.playerInternalData.popSystemData().addCarrier(newCarrier)

        playerData.playerInternalData.physicsData().coreRestMass += newCarrier.carrierInternalData.coreRestMass
    }
}