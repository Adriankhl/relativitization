package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DisableFuelIncreaseCommand
import relativitization.universe.data.component.physics.Double3D
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
    val targetDouble3D: Double3D,
    val maxSpeed: Double,
    override val stayTime: Int,
) : Event() {

    override val description: I18NString = I18NString(
        listOf(
            RealString("Player "),
            IntString(0),
            RealString(" moving to"),
            IntString(1),
        ),
        listOf(
            toId.toString(),
            targetDouble3D.toString()
        ),
    )

    override val choiceDescription: Map<Int, String> = mapOf(
        0 to "Moving to position $targetDouble3D",
        1 to "Cancel this command"
    )

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.isSubOrdinateOrSelf(toId)
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.isLeaderOrSelf(fromId)
    }

    override fun generateCommands(choice: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
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
                maxDeltaRestMass = playerData.playerInternalData.physicsData().maxDeltaRestMass(),
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


    override fun defaultChoice(universeData3DAtPlayer: UniverseData3DAtPlayer): Int {
        return 0
    }

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        return if (mutableEventData.choice == 1) {
            true
        } else {
            val sameDouble3D: Boolean = universeData3DAtPlayer.getCurrentPlayerData().double4D.toDouble3D() == targetDouble3D
            val zeroVelocity: Boolean = universeData3DAtPlayer.getCurrentPlayerData().velocity.mag() <= 0.0

            sameDouble3D && zeroVelocity
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}