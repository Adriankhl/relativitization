package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.CommandErrorMessage
import relativitization.universe.data.commands.CommandI18NStringFactory
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Double3D
import relativitization.universe.maths.physics.Movement
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Automatically change player velocity to move to a location
 * The player should not increase their fuel mass to calculate acceleration correctly
 */
@Serializable
data class MoveToDouble3DEvent(
    override val toId: Int,
    override val fromId: Int,
    val targetDouble3D: Double3D,
    val maxSpeed: Double,
) : DefaultEvent() {

    override fun description(): I18NString = I18NString(
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

    override fun choiceDescription(): Map<Int, I18NString> = mapOf(
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
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isEventUnique = CommandErrorMessage(
            playerData.playerInternalData.eventDataMap.filterValues {
                it.event is MoveToDouble3DEvent
            }.values.all {
                it.event.fromId != fromId
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

    override fun stayTime(): Int = if (fromId == toId) {
        0
    } else {
        5
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
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
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings
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