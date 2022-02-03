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
import relativitization.universe.utils.NormalString
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

    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" moving to "),
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
                NormalString("Moving to position "),
                IntString(0),
            ),
            listOf(
                targetDouble3D.toString()
            )
        ),
        1 to I18NString(
            listOf(NormalString("Cancel this command")),
            listOf()
        )
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
                isLeaderOrSelf,
                hasFuel,
            )
        )
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        // only if counter > 0, skip first turn to allow player choose
        return if ((mutableEventRecordData.stayCounter > 0) && (mutableEventRecordData.choice == 0)) {
            val playerData: PlayerData = universeData3DAtPlayer.getCurrentPlayerData()

            if (maxSpeed > universeData3DAtPlayer.universeSettings.speedOfLight) {
                logger.error("maxSpeed greater than the speed of light")
            }

            // disable fuel production by one turn
            val disableFuelIncreaseCommand = DisableFuelIncreaseCommand(
                toId = playerData.playerId,
                fromId = playerData.playerId,
                fromInt4D = playerData.int4D,
                disableFuelIncreaseTimeLimit = 1,
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
                toId = toId,
                fromId = toId,
                fromInt4D = universeData3DAtPlayer.get(toId).int4D,
                targetVelocity = targetVelocityData.newVelocity
            )

            changeVelocityCommand.checkAndExecute(
                mutablePlayerData,
                universeData3DAtPlayer.universeSettings
            )
            disableFuelIncreaseCommand.checkAndExecute(
                mutablePlayerData,
                universeData3DAtPlayer.universeSettings
            )

            listOf()
        } else {
            listOf()
        }
    }


    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Int {
        val eventDataMap: Map<Int, EventData> = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData
            .eventDataMap

        val otherMovementEventMap: Map<Int, EventData> = eventDataMap.filter { (id, eventData) ->
            (eventData.event is MoveToDouble3DEvent) && (id != eventId)
        }
        return if (otherMovementEventMap.isEmpty()) {
            0
        } else {
            if (otherMovementEventMap.any { it.value.eventRecordData.hasChoice }) {
                1
            } else {
                val leaderAndSelfIdList: List<Int> = universeData3DAtPlayer.getCurrentPlayerData()
                    .getLeaderAndSelfIdList()

                val keepMovementEventId: Int = otherMovementEventMap.maxByOrNull {
                    leaderAndSelfIdList.indexOf(it.value.event.fromId)
                }!!.key

                if (keepMovementEventId == eventId) {
                    0
                } else {
                    1
                }
            }
        }
    }

    override fun shouldCancelThisEvent(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        return if (mutableEventRecordData.hasChoice && (mutableEventRecordData.choice == 1)) {
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