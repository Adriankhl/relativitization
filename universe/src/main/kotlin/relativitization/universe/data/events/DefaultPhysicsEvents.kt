package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.physics.Double3D
import relativitization.universe.maths.physics.Movement
import relativitization.universe.maths.physics.Movement.targetDouble3DByPhotonRocket
import relativitization.universe.maths.physics.TargetVelocityData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min

/**
 * Automatically change player velocity to move to a location
 * The player should not increase their fuel mass to calculate acceleration correctly
 */
@Serializable
data class MoveToDouble3DEvent(
    override val toId: Int,
    override val fromId: Int,
    override val stayTime: Int,
    val targetDouble3D: Double3D,
    val maxSpeed: Double,
) : DefaultEvent() {

    override val description: I18NString = I18NString(
        listOf(
            RealString("Player "),
            IntString(0),
            RealString(" moving to "),
            IntString(1),
        ),
        listOf(
            toId.toString(),
            targetDouble3D.toString()
        ),
    )

    override val choiceDescription: Map<Int, I18NString> = mapOf(
        0 to I18NString(
            listOf(
                RealString("Moving to position "),
                IntString(0),
            ),
            listOf(
                targetDouble3D.toString()
            )
        ),
        1 to I18NString(
            listOf(RealString("Cancel this command")),
            listOf()
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isSubOrdinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        return if (isSubOrdinateOrSelf) {
            CanSendCheckMessage(true)
        } else {
            CanSendCheckMessage(
                false,
                CanSendCheckMessageI18NStringFactory.isNotSubordinate(playerData.playerId, toId)
            )
        }
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val requiredDeltaRestMass: Double = Movement.requiredDeltaRestMassUpperBound(
            initialRestMass = playerData.playerInternalData.physicsData().totalRestMass(),
            maxDeltaRestMass = playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDelta,
            initialVelocity = playerData.velocity.toVelocity(),
            maxSpeed = maxSpeed,
            initialDouble3D = playerData.double4D.toDouble3D(),
            targetDouble3D = targetDouble3D,
            speedOfLight = universeSettings.speedOfLight,
            numIteration = 100
        )
        return (requiredDeltaRestMass <=
                playerData.playerInternalData.physicsData().fuelRestMassData.movement) &&
                (playerData.isLeaderOrSelf(fromId))
    }

    override fun generateCommands(
        eventId: Int,
        choice: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        val playerData: PlayerData = universeData3DAtPlayer.getCurrentPlayerData()

        if (maxSpeed > universeData3DAtPlayer.universeSettings.speedOfLight) {
            logger.error("maxSpeed greater than the speed of light")
        }

        return if (choice == 0) {
            // disable fuel production by one turn
            val disableFuelIncreaseCommand = DisableFuelIncreaseCommand(
                disableFuelIncreaseTimeLimit = 1,
                toId = playerData.playerId,
                fromId = playerData.playerId,
                fromInt4D = playerData.int4D,
            )

            val targetVelocityData: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = playerData.playerInternalData.physicsData().totalRestMass(),
                maxDeltaRestMass = playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDeltaRestMass(),
                initialVelocity = playerData.velocity,
                maxSpeed = min(maxSpeed, universeData3DAtPlayer.universeSettings.speedOfLight),
                initialDouble3D = playerData.double4D.toDouble3D(),
                targetDouble3D = targetDouble3D,
                speedOfLight = universeData3DAtPlayer.universeSettings.speedOfLight
            )

            val changeVelocityCommand = ChangeVelocityCommand(
                targetVelocity = targetVelocityData.newVelocity,
                toId = toId,
                fromId = toId,
                fromInt4D = universeData3DAtPlayer.get(toId).int4D
            )
            listOf(changeVelocityCommand, disableFuelIncreaseCommand)
        } else {
            listOf()
        }
    }


    override fun defaultChoice(
        eventId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Int {
        val eventDataMap: Map<Int, EventData> =
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap

        val otherMovementEvents: Map<Int, EventData> = eventDataMap.filter { (id, eventData) ->
            (eventData.event is MoveToDouble3DEvent) && (id != eventId)
        }
        return if (otherMovementEvents.isEmpty()) {
            0
        } else {
            if (otherMovementEvents.any { it.value.hasChoice }) {
                1
            } else {
                val leaderIdList: List<Int> =
                    universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.leaderIdList

                // if all movement events are default, find the one with the closest relation with the player
                val entry = eventDataMap.maxByOrNull { (_, eventData) ->
                    if (leaderIdList.contains(eventData.event.fromId)) {
                        leaderIdList.indexOf(eventData.event.fromId)
                    } else {
                        -1
                    }
                }

                val entryId: Int = entry?.key ?: eventId

                if (entryId == eventId) {
                    0
                } else {
                    1
                }
            }
        }
    }

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        return if (mutableEventData.choice == 1) {
            true
        } else {
            val sameDouble3D: Boolean =
                universeData3DAtPlayer.getCurrentPlayerData().double4D.toDouble3D() == targetDouble3D
            val zeroVelocity: Boolean =
                universeData3DAtPlayer.getCurrentPlayerData().velocity.mag() <= 0.0

            sameDouble3D && zeroVelocity
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}