package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.*
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import relativitization.universe.maths.physics.TargetVelocityData
import relativitization.universe.utils.*

@Serializable
data class ChangeVelocityCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetVelocity: Velocity,
) : DefaultCommand() {

    override fun description(): I18NString = I18NString(
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

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Unit {
        val speedOfLight: Double = universeSettings.speedOfLight

        if (targetVelocity.mag() < speedOfLight) {

            val targetVelocityData: TargetVelocityData = targetVelocityByPhotonRocket(
                initialRestMass = playerData.playerInternalData.physicsData().totalRestMass(),
                maxDeltaRestMass = playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDeltaRestMass(),
                initialVelocity = playerData.velocity.toVelocity(),
                targetVelocity = targetVelocity,
                speedOfLight = universeSettings.speedOfLight
            )

            playerData.velocity.vx = targetVelocityData.newVelocity.vx
            playerData.velocity.vy = targetVelocityData.newVelocity.vy
            playerData.velocity.vz = targetVelocityData.newVelocity.vz

            playerData.playerInternalData.physicsData().fuelRestMassData.movement -= targetVelocityData.deltaRestMass
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
 * @property targetFuelRestMassProportionData the target proportion
 */
@Serializable
data class ChangeTargetFuelRestMassProportionCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetFuelRestMassProportionData: TargetFuelRestMassProportionData,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
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
            targetFuelRestMassProportionData.storage.toString(),
            targetFuelRestMassProportionData.movement.toString(),
            targetFuelRestMassProportionData.production.toString(),
            targetFuelRestMassProportionData.trade.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
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

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.physicsData().targetFuelRestMassProportionData =
            DataSerializer.copy(targetFuelRestMassProportionData)
    }
}