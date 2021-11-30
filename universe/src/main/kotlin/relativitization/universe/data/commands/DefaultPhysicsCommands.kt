package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import relativitization.universe.maths.physics.TargetVelocityData
import relativitization.universe.utils.*

@Serializable
data class ChangeVelocityCommand(
    val targetVelocity: Velocity,
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : DefaultCommand() {

    override val description: I18NString = I18NString(
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
    ): CanSendCheckMessage {
        val isSubordinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        val isSubordinateOrSelfI18NString: I18NString = if (isSubordinateOrSelf) {
            I18NString("")
        } else {
            I18NString("Not subordinate or self.")
        }

        return CanSendCheckMessage(
            isSubordinateOrSelf,
            listOf(
                isSubordinateOrSelfI18NString
            )
        )
    }

    /**
     * Can execute only if it is from the leader
     */
    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.isLeaderOrSelf(fromId)
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
 * Transfer fuel from storage to production
 *
 * @property amount the amount of resource to transfer
 */
@Serializable
data class TransferFuelToProductionCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Transfer "),
            IntString(0),
            NormalString(" of fuel from storage to production. "),
        ),
        listOf(
            amount.toString(),
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

        val hasStorage: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount
        val hasStorageI18NString: I18NString = if (hasStorage) {
            I18NString("")
        } else {
            I18NString("Not enough fuel in storage. ")
        }

        return CanSendCheckMessage(
            isSelf && hasStorage,
            listOf(
                isSelfI18NString,
                hasStorageI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == toId

        val hasStorage: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount

        return isSelf && hasStorage
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val fuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().fuelRestMassData

        fuelData.storage -= amount
        fuelData.production += amount
    }
}