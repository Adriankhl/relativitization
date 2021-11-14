package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.modifier.CombatModifierData
import relativitization.universe.data.components.modifier.MutableCombatModifierData
import relativitization.universe.data.components.modifier.MutablePhysicsModifierData
import relativitization.universe.data.components.modifier.PhysicsModifierData

@Serializable
@SerialName("ModifierData")
data class ModifierData(
    val physicsModifierData: PhysicsModifierData = PhysicsModifierData(),
    val combatModifierData: CombatModifierData = CombatModifierData(),
) : PlayerDataComponent()

@Serializable
@SerialName("ModifierData")
data class MutableModifierData(
    var physicsModifierData: MutablePhysicsModifierData = MutablePhysicsModifierData(),
    var combatModifierData: MutableCombatModifierData = MutableCombatModifierData(),
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
        combatModifierData.updateByUniverseTime()
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    private fun updateByProperTime(gamma: Double) {
        physicsModifierData.updateByProperTime(gamma)
        combatModifierData.updateByProperTime(gamma)
    }
}