package relativitization.universe.data.modifier

import kotlinx.serialization.Serializable

@Serializable
data class ModifierData(
    val physicsModifierData: PhysicsModifierData = PhysicsModifierData()
)

@Serializable
data class MutableModifierData(
    val physicsModifierData: MutablePhysicsModifierData = MutablePhysicsModifierData()
) {
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