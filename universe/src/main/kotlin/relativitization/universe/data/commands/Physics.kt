package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
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
    override val fromId: Int,
    override val toId: Int,
    override val fromInt4D: Int4D,
    val velocity: Velocity,
) : Command() {

    override val name: String = "ChangeVelocity"

    override fun description(): String {
        return "Try to change velocity of player $toId to $Velocity"
    }

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.playerInternalData.subordinateIdList.contains(toId)
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.playerInternalData.leaderIdList.contains(fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Unit {
        val speedOfLight: Int = universeSettings.speedOfLight

        val restMass: Double = playerData.playerInternalData.physicsData.restMass
        val efficiency: Double = playerData.playerInternalData.physicsData.moveEnergyEfficiency
        val originalVelocity: Velocity = copy(playerData.playerInternalData.physicsData.velocity)
        val originalEnergy: Double = Relativistic.energy(restMass, originalVelocity, speedOfLight)

        // max power of remaining energy
        val energyAvailable = max(
            playerData.playerInternalData.physicsData.energy.toActualEnergyUnit(speedOfLight),
            playerData.playerInternalData.physicsData.moveMaxPower.toActualEnergyUnit(speedOfLight)
        )

        // same energy for acceleration and deceleration
        val required = abs(Relativistic.energy(restMass, velocity, speedOfLight) - originalEnergy)
        val isAcceleration: Boolean = Relativistic.energy(restMass, velocity, speedOfLight) > originalEnergy

        val newVelocity: Velocity = when {
            required < energyAvailable * efficiency -> {
                velocity
            }
            isAcceleration -> {
                val vMag = Relativistic.energyToVelocityMag(
                    restMass,
                    energyAvailable * efficiency + originalEnergy,
                    speedOfLight
                )
                velocity.scaleVelocity(vMag)
            }
            else -> {
                val vMag = Relativistic.energyToVelocityMag(
                    restMass,
                    originalEnergy - energyAvailable * efficiency,
                    speedOfLight
                )
                velocity.scaleVelocity(vMag)
            }
        }

        val energyUsed = abs(Relativistic.energy(restMass, newVelocity, speedOfLight) - originalEnergy) / efficiency

        playerData.playerInternalData.physicsData.velocity = copy(newVelocity)
        playerData.playerInternalData.physicsData.energy -= energyUsed.toStandardEnergyUnit(speedOfLight)
    }
}