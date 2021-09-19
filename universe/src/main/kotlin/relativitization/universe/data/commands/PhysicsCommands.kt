package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import relativitization.universe.maths.physics.TargetVelocityData
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class ChangeVelocityCommand(
    val targetVelocity: Velocity,
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : Command() {

    override val description: I18NString = I18NString(
        listOf(
            RealString("Try to change velocity of player "),
            IntString(0),
            RealString(" to "),
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
    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendWithMessage {
        val isSubOrdinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        return if (isSubOrdinateOrSelf) {
            CanSendWithMessage(true)
        } else {
            CanSendWithMessage(
                false,
                CanSendWIthMessageI18NStringFactory.isNotSubordinate(playerData.playerId, toId)
            )
        }
    }

    /**
     * Can execute only if it is from the leader
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
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