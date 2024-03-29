package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.maths.physics.Relativistic.targetVelocityByPhotonRocket
import relativitization.universe.core.maths.physics.TargetVelocityData
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.physics.FuelRestMassTargetProportionData
import relativitization.universe.game.data.components.defaults.physics.maxMovementDeltaRestMass
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.totalRestMass
import kotlin.math.min

@Serializable
data class ChangeVelocityCommand(
    override val toId: Int,
    val targetVelocity: Velocity,
) : DefaultCommand() {
    override fun name(): String = "Change Velocity"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Try to change velocity of player "),
            IntString(0),
            NormalString(" to "),
            IntString(1),
        ),
        listOf(
            toId.toString(),
            targetVelocity.toString(),
        ),
    )

    /**
     * Can only send to subordinate
     */
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSubordinateOrSelf = CommandErrorMessage(
            playerData.isSubOrdinateOrSelf(toId),
            I18NString("Not subordinate or self.")
        )

        val isVelocityValid = CommandErrorMessage(
            targetVelocity.mag() < universeSettings.speedOfLight,
            I18NString("Target speed is larger than the speed of light")
        )

        return CommandErrorMessage(
            listOf(
                isSubordinateOrSelf,
                isVelocityValid,
            )
        )
    }

    /**
     * Can execute only if it is from the leader
     */
    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isLeaderOrSelf = CommandErrorMessage(
            playerData.isLeaderOrSelf(fromId),
            I18NString("Sender is not leader or self. ")
        )

        return CommandErrorMessage(
            listOf(
                isLeaderOrSelf
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val speedOfLight: Double = universeSettings.speedOfLight

        if (targetVelocity.mag() < speedOfLight) {

            val targetVelocityData: TargetVelocityData = targetVelocityByPhotonRocket(
                initialRestMass = playerData.playerInternalData.physicsData().totalRestMass(),
                maxDeltaRestMass = min(
                    playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDeltaRestMass(),
                    playerData.playerInternalData.physicsData().fuelRestMassData.movement,
                ),
                initialVelocity = playerData.velocity.toVelocity(),
                targetVelocity = targetVelocity,
                speedOfLight = universeSettings.speedOfLight
            )

            playerData.velocity.vx = targetVelocityData.newVelocity.vx
            playerData.velocity.vy = targetVelocityData.newVelocity.vy
            playerData.velocity.vz = targetVelocityData.newVelocity.vz

            playerData.playerInternalData.physicsData().removeExternalMovementFuel(targetVelocityData.deltaRestMass)
        } else {
            logger.error("Target velocity larger than the speed of light")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Change the storage fuel target amount
 *
 * @property fuelRestMassTargetProportionData the target proportion
 */
@Serializable
data class ChangeFuelRestMassTargetProportionCommand(
    override val toId: Int,
    val fuelRestMassTargetProportionData: FuelRestMassTargetProportionData,
) : DefaultCommand() {
    override fun name(): String = "Change Fuel Target"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Change the target fuel rest mass proportion to storage: "),
            IntString(0),
            NormalString(", movement: "),
            IntString(1),
            NormalString(", production: "),
            IntString(2),
            NormalString(", trade: "),
            IntString(3),
            NormalString(". "),
        ),
        listOf(
            fuelRestMassTargetProportionData.storage.toString(),
            fuelRestMassTargetProportionData.movement.toString(),
            fuelRestMassTargetProportionData.production.toString(),
            fuelRestMassTargetProportionData.trade.toString(),
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

        return CommandErrorMessage(
            listOf(
                isSelf
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
        playerData.playerInternalData.physicsData().fuelRestMassTargetProportionData =
            DataSerializer.copy(fuelRestMassTargetProportionData)
    }
}