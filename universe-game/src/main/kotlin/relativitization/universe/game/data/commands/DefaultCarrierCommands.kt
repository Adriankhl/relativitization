package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString

/**
 * Build a new carrier locally, can only send to self
 */
@Serializable
data class BuildLocalCarrierCommand(
    override val toId: Int,
    val qualityLevel: Double,
) : DefaultCommand() {
    override fun name(): String = "Build Local Carrier"
    override fun description(fromId: Int): I18NString = I18NString(
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {

        val newCarrierInternalData: MutableCarrierInternalData =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newSpaceshipInternalData(
                qualityLevel = qualityLevel
            )
        val newCarrier = MutableCarrierData(
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