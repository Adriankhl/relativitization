package relativitization.universe.data.subsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.modifier.MutablePhysicsModifierData
import relativitization.universe.data.subsystem.modifier.PhysicsModifierData

@Serializable
data class ModifierData(
    val physicsModifierData: PhysicsModifierData = PhysicsModifierData()
) : PlayerSubsystemData

@Serializable
data class MutableModifierData(
    val physicsModifierData: MutablePhysicsModifierData = MutablePhysicsModifierData()
) : MutablePlayerSubsystemData {
    /**
     * Decrease time of modifiers
     */
    fun updateModifierTime(gamma: Double) {
        updateByUniverseTime()
        updateByProperTime(gamma)
    }

    /**
     * Update the time by universe time
     */
    private fun updateByUniverseTime() {
        physicsModifierData.updateByUniverseTime()
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    private fun updateByProperTime(gamma: Double) {
        physicsModifierData.updateByProperTime(gamma)
    }
}