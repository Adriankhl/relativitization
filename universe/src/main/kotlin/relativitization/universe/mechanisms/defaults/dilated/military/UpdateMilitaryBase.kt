package relativitization.universe.mechanisms.defaults.dilated.military

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.defaults.modifier.MutableCombatModifierData
import relativitization.universe.data.components.defaults.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min

object UpdateMilitaryBase : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Affect the size of the shield compare to typical attack
        val maxShieldFactor: Double = 5.0
        // Affect how fast the shield recharge
        val shieldChangeFactor: Double = 0.05

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val newAttack: Double = computeMilitaryBaseAttack(
                soldierPopData = mutableCarrierData.allPopData.soldierPopData,
                playerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
            )

            val newShield: Double = computeMilitaryBaseShield(
                soldierPopData = mutableCarrierData.allPopData.soldierPopData,
                playerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                maxShieldFactor = maxShieldFactor,
                shieldChangeFactor = shieldChangeFactor,
                combatModifierData = mutablePlayerData.playerInternalData.modifierData().combatModifierData,
            )

            mutableCarrierData.allPopData.soldierPopData.militaryBaseData.attack = newAttack
            mutableCarrierData.allPopData.soldierPopData.militaryBaseData.attack = newShield

        }

        return listOf()
    }

    fun computeMilitaryBaseAttack(
        soldierPopData: MutableSoldierPopData,
        playerScienceData: MutablePlayerScienceData
    ): Double {

        val satisfactionFactor: Double = if (soldierPopData.commonPopData.satisfaction > 1.0) {
            1.0
        } else {
            soldierPopData.commonPopData.satisfaction
        }

        // Can only attack if shield is greater than 0
        return if (soldierPopData.militaryBaseData.shield > 0.0) {
            soldierPopData.militaryBaseData.lastNumEmployee * satisfactionFactor * playerScienceData.playerScienceApplicationData.militaryBaseAttackFactor
        } else {
            0.0
        }
    }

    fun computeMilitaryBaseShield(
        soldierPopData: MutableSoldierPopData,
        playerScienceData: MutablePlayerScienceData,
        maxShieldFactor: Double = 5.0,
        shieldChangeFactor: Double,
        combatModifierData: MutableCombatModifierData,
    ): Double {

        val originalShield: Double = soldierPopData.militaryBaseData.shield

        val satisfactionFactor: Double = if (soldierPopData.commonPopData.satisfaction > 1.0) {
            1.0
        } else {
            soldierPopData.commonPopData.satisfaction
        }

        val maxShield: Double =
            soldierPopData.militaryBaseData.lastNumEmployee * satisfactionFactor * playerScienceData.playerScienceApplicationData.militaryBaseShieldFactor * maxShieldFactor

        // Compute the change in two ways: fraction of the difference or fraction of the max shield
        // take the larger one
        val change: Double = when {
            originalShield > maxShield * 2.0 -> {
                (maxShield - originalShield) * shieldChangeFactor
            }
            originalShield > maxShield -> {
                -min(maxShield * shieldChangeFactor, originalShield - maxShield)
            }
            else -> {
                if (combatModifierData.disableMilitaryBaseRecoveryTimeLimit <= 0) {
                    min(maxShield * shieldChangeFactor, maxShield - originalShield)
                } else {
                    originalShield
                }
            }
        }

        // Adjusted by time dilation
        return originalShield + change
    }
}