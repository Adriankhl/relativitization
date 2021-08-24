package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.component.modifier.MutablePhysicsModifierData
import relativitization.universe.data.component.modifier.PhysicsModifierData

@Serializable
@SerialName("ModifierData")
data class ModifierData(
    val physicsModifierData: PhysicsModifierData = PhysicsModifierData()
) : PlayerDataComponent()

@Serializable
@SerialName("ModifierData")
data class MutableModifierData(
    val physicsModifierData: MutablePhysicsModifierData = MutablePhysicsModifierData()
) : MutablePlayerDataComponent() {
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