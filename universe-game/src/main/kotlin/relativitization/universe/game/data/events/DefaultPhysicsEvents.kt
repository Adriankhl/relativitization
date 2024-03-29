package relativitization.universe.game.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Double3D
import relativitization.universe.core.maths.physics.Movement
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.totalRestMass
import kotlin.random.Random

/**
 * Automatically change player velocity to move to a location
 * The player should not increase their fuel mass to calculate acceleration correctly
 */
@Serializable
data class MoveToDouble3DEvent(
    override val toId: Int,
    val targetDouble3D: Double3D,
    val maxSpeed: Double,
) : DefaultEvent() {
    override fun name(): String = "Move To Location"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" ask player "),
            IntString(1),
            NormalString(" move to "),
            IntString(2),
            NormalString(". "),
        ),
        listOf(
            fromId.toString(),
            toId.toString(),
            targetDouble3D.toString()
        ),
    )

    override fun choiceDescription(fromId: Int): Map<Int, I18NString> = mapOf(
        0 to I18NString("Accept"),
        1 to I18NString("Reject")
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSubOrdinateOrSelf = CommandErrorMessage(
            playerData.isSubOrdinateOrSelf(toId),
            CommandI18NStringFactory.isNotSubordinate(playerData.playerId, toId)
        )

        val isMaxSpeedValid = CommandErrorMessage(
            maxSpeed <= universeSettings.speedOfLight,
            I18NString("Target max. speed is larger than the speed of light")
        )

        return CommandErrorMessage(
            listOf(
                isSubOrdinateOrSelf,
                isMaxSpeedValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is MoveToDouble3DEvent
            }.values.all {
                it.fromId != fromId
            },
            I18NString("Event already exists. ")
        )

        val isLeaderOrSelf = CommandErrorMessage(
            playerData.isLeaderOrSelf(fromId),
            I18NString("Sender is not leader or self. ")
        )

        val requiredDeltaRestMass: Double = Movement.requiredDeltaRestMassUpperBound(
            initialRestMass = playerData.playerInternalData.physicsData().totalRestMass(),
            maxDeltaRestMassPerTurn = playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDelta,
            initialVelocity = playerData.velocity.toVelocity(),
            maxSpeed = maxSpeed,
            initialDouble3D = playerData.double4D.toDouble3D(),
            targetDouble3D = targetDouble3D,
            speedOfLight = universeSettings.speedOfLight,
            numIteration = 100
        )

        val hasFuel = CommandErrorMessage(
            requiredDeltaRestMass <= playerData.playerInternalData.physicsData().fuelRestMassData.movement,
            I18NString("Not enough fuel. ")
        )


        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isLeaderOrSelf,
                hasFuel,
            )
        )
    }

    override fun stayTime(fromId: Int): Int = if (fromId == toId) {
        0
    } else {
        5
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Boolean {
        val sameDouble3D: Boolean =
            universeData3DAtPlayer.getCurrentPlayerData().double4D.toDouble3D() == targetDouble3D
        val zeroVelocity: Boolean =
            universeData3DAtPlayer.getCurrentPlayerData().velocity.mag() <= 0.0

        return sameDouble3D && zeroVelocity
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            mutablePlayerData.playerInternalData.physicsData().targetDouble3DData.hasTarget = true

            mutablePlayerData.playerInternalData.physicsData().targetDouble3DData.commanderId =
                fromId

            mutablePlayerData.playerInternalData.physicsData().targetDouble3DData.maxSpeed =
                maxSpeed

            mutablePlayerData.playerInternalData.physicsData().targetDouble3DData.target =
                DataSerializer.copy(targetDouble3D)

            listOf()
        },
        1 to { listOf() }
    )


    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        random: Random
    ): Int {
        return if (fromId == mutablePlayerData.playerId) {
            0
        } else {
            1
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}