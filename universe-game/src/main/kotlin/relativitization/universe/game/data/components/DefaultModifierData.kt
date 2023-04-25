package relativitization.universe.game.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.game.data.components.defaults.modifier.CombatModifierData
import relativitization.universe.game.data.components.defaults.modifier.DiplomacyModifierData
import relativitization.universe.game.data.components.defaults.modifier.MutableCombatModifierData
import relativitization.universe.game.data.components.defaults.modifier.MutableDiplomacyModifierData
import relativitization.universe.game.data.components.defaults.modifier.MutablePhysicsModifierData
import relativitization.universe.game.data.components.defaults.modifier.PhysicsModifierData

@Serializable
@SerialName("ModifierData")
data class ModifierData(
    val physicsModifierData: PhysicsModifierData = PhysicsModifierData(),
    val combatModifierData: CombatModifierData = CombatModifierData(),
    val diplomacyModifierData: DiplomacyModifierData = DiplomacyModifierData(),
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("ModifierData")
data class MutableModifierData(
    var physicsModifierData: MutablePhysicsModifierData = MutablePhysicsModifierData(),
    var combatModifierData: MutableCombatModifierData = MutableCombatModifierData(),
    var diplomacyModifierData: MutableDiplomacyModifierData = MutableDiplomacyModifierData(),
) : MutableDefaultPlayerDataComponent() {
    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        physicsModifierData.updateByUniverseTime()
        combatModifierData.updateByUniverseTime()
        diplomacyModifierData.updateByUniverseTime()
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime() {
        physicsModifierData.updateByProperTime()
        combatModifierData.updateByProperTime()
        diplomacyModifierData.updateByProperTime()
    }
}

fun PlayerInternalData.modifierData(): ModifierData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.modifierData(): MutableModifierData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.modifierData(newModifierData: MutableModifierData) =
    playerDataComponentMap.put(newModifierData)