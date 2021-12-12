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
    ): CommandErrorMessage {
        val isSubordinateOrSelf = CommandErrorMessage(
            playerData.isSubOrdinateOrSelf(toId),
            I18NString("Not subordinate or self.")
        )

        val isVelocityValid = CommandErrorMessage(
            targetVelocity.mag() <= universeSettings.speedOfLight,
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
 * Transfer fuel from storage to movement
 *
 * @property amount the amount of resource to transfer
 */
@Serializable
data class TransferFuelToMovementCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Transfer "),
            IntString(0),
            NormalString(" of fuel from storage to movement. "),
        ),
        listOf(
            amount.toString(),
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

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage,
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

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val fuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().fuelRestMassData

        fuelData.storage -= amount
        fuelData.movement += amount
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage,
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

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val fuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().fuelRestMassData

        fuelData.storage -= amount
        fuelData.production += amount
    }
}

/**
 * Transfer fuel from storage to trade
 *
 * @property amount the amount of resource to transfer
 */
@Serializable
data class TransferFuelToTradeCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Transfer "),
            IntString(0),
            NormalString(" of fuel from storage to trade. "),
        ),
        listOf(
            amount.toString(),
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

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage,
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

        val hasStorage = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString("Not enough fuel in storage. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasStorage
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val fuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().fuelRestMassData

        fuelData.storage -= amount
        fuelData.trade += amount
    }
}

/**
 * Change the storage fuel target amount
 *
 * @property targetAmount the target amount of fuel
 */
@Serializable
data class ChangeStorageFuelTargetCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetAmount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the target amount of fuel for storage to "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            targetAmount.toString(),
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
        val targetFuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().targetFuelRestMassData

        targetFuelData.storage = targetAmount
    }
}

/**
 * Change the movement fuel target amount
 *
 * @property targetAmount the target amount of fuel
 */
@Serializable
data class ChangeMovementFuelTargetCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetAmount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the target amount of fuel for movement to "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            targetAmount.toString(),
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
        val targetFuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().targetFuelRestMassData

        targetFuelData.movement = targetAmount
    }
}

/**
 * Change the production fuel target amount
 *
 * @property targetAmount the target amount of fuel
 */
@Serializable
data class ChangeProductionFuelTargetCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetAmount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the target amount of fuel for production to "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            targetAmount.toString(),
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
        val targetFuelData: MutableFuelRestMassData =
            playerData.playerInternalData.physicsData().targetFuelRestMassData

        targetFuelData.production = targetAmount
    }
}