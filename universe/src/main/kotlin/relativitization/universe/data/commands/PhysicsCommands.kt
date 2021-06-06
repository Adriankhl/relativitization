package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Relativistic.toActualEnergyUnit
import relativitization.universe.maths.physics.Relativistic.toStandardEnergyUnit
import kotlin.math.abs
import kotlin.math.max

@Serializable
data class ChangeVelocityCommand(
    val targetVelocity: Velocity,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int,
) : Command() {

    override val name: String = "Change Velocity"

    override fun description(): String {
        return "Try to change velocity of player $toId to $targetVelocity"
    }

    /**
     * Can only send to subordinate
     */
    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.isSubOrdinateOrSelf(toId)
    }

    /**
     * Can execute only if it is from the leader
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.isLeaderOrSelf(fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Unit {
        val speedOfLight: Double = universeSettings.speedOfLight

        if (targetVelocity.squareMag() <= speedOfLight * speedOfLight) {
            val restMass: Double = playerData.playerInternalData.physicsData.restMass
            val efficiency: Double = playerData.playerInternalData.physicsData.moveEnergyEfficiency
            val originalVelocity: Velocity = copy(playerData.velocity)
            val originalEnergy: Double = Relativistic.energy(restMass, originalVelocity, speedOfLight)

            // max power of remaining energy
            val energyAvailable = max(
                playerData.playerInternalData.physicsData.energy.toActualEnergyUnit(speedOfLight),
                playerData.playerInternalData.physicsData.moveMaxPower.toActualEnergyUnit(speedOfLight)
            )

            // same energy for acceleration and deceleration
            val required = abs(Relativistic.energy(restMass, targetVelocity, speedOfLight) - originalEnergy)
            val isAcceleration: Boolean = Relativistic.energy(restMass, targetVelocity, speedOfLight) > originalEnergy

            val newVelocity: Velocity = when {
                required < energyAvailable * efficiency -> {
                    targetVelocity
                }
                isAcceleration -> {
                    val vMag = Relativistic.energyToVelocityMag(
                        restMass,
                        energyAvailable * efficiency + originalEnergy,
                        speedOfLight
                    )
                    targetVelocity.scaleVelocity(vMag)
                }
                else -> {
                    val vMag = Relativistic.energyToVelocityMag(
                        restMass,
                        originalEnergy - energyAvailable * efficiency,
                        speedOfLight
                    )
                    targetVelocity.scaleVelocity(vMag)
                }
            }

            val energyUsed = abs(Relativistic.energy(restMass, newVelocity, speedOfLight) - originalEnergy) / efficiency

            playerData.velocity = copy(newVelocity)
            playerData.playerInternalData.physicsData.energy -= energyUsed.toStandardEnergyUnit(speedOfLight)
        } else {
            logger.error("Target velocity larger than the speed of light")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}