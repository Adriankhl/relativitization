package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.modifier.*

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
    playerDataComponentMap.getOrDefault(ModifierData::class, ModifierData())

fun MutablePlayerInternalData.modifierData(): MutableModifierData =
    playerDataComponentMap.getOrDefault(MutableModifierData::class, MutableModifierData())

fun MutablePlayerInternalData.modifierData(newModifierData: MutableModifierData) =
    playerDataComponentMap.put(newModifierData)