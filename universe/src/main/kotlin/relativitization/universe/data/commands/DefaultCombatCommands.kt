package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.modifierData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.maths.random.Rand
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Damage from one player to another, send by auto combat mechanism only
 *
 * @property attack attack from player
 */
@Serializable
data class DamageCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val attack: Double,
) : DefaultCommand() {
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        // Disable military base recovery by 1 turn
        playerData.playerInternalData.modifierData().combatModifierData.disableMilitaryBaseRecoveryByTime(
            1
        )

        val carrierIdList: MutableList<Int> = playerData.playerInternalData.popSystemData().carrierDataMap.keys
            .shuffled(Rand.rand()).toMutableList()

        // Use attack to destroy carrier, until used up or no carrier left
        var attackAcc: Double = attack

        // Attack consume shield
        carrierIdList.forEach {
            val carrierData: MutableCarrierData = playerData.playerInternalData.popSystemData().carrierDataMap
                .getValue(it)

            val shield: Double = carrierData.allPopData.soldierPopData.militaryBaseData.shield

            if (shield > attackAcc) {
                carrierData.allPopData.soldierPopData.militaryBaseData.shield -= attackAcc
                attackAcc = 0.0
            } else {
                if (carrierData.carrierType == CarrierType.SPACESHIP) {
                    // Destroy this carrier
                    playerData.playerInternalData.popSystemData().carrierDataMap.remove(it)

                    // Remove rest mass of the carrier
                    playerData.playerInternalData.physicsData().coreRestMass -=
                        carrierData.carrierInternalData.coreRestMass
                    playerData.playerInternalData.physicsData().otherRestMass -= carrierData.totalOtherRestMass()
                } else {
                    carrierData.allPopData.soldierPopData.militaryBaseData.shield = 0.0
                }

                attackAcc -= shield
            }

            if (attackAcc <= 0.0) return@forEach
        }

        // player is dead if no carrier
        if (playerData.playerInternalData.popSystemData().carrierDataMap.isEmpty()) {
            playerData.playerInternalData.isAlive = false
        } else {
            // Change leader if all carrier shields are destroyed
            val allCarrierDestroyed: Boolean =
                playerData.playerInternalData.popSystemData().carrierDataMap.values.all {
                    (it.carrierType != CarrierType.SPACESHIP) && (it.allPopData.soldierPopData.militaryBaseData.shield <= 0.0)
                }

            if (allCarrierDestroyed) {
                // Don't change leader id if the damage is from subordinate
                if (playerData.playerInternalData.subordinateIdSet.contains(fromId)) {
                    logger.debug("Destroyed by subordinate $fromId")
                } else {
                    playerData.changeDirectLeader(listOf(fromId))
                }
            }
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}
