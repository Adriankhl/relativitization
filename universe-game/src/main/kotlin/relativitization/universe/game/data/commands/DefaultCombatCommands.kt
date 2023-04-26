package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.game.data.components.defaults.popsystem.pop.totalAdultPopulation
import relativitization.universe.game.data.components.defaults.popsystem.totalOtherRestMass
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData

/**
 * Damage from one player to another, send by auto combat mechanism only
 *
 * @property attack attack from player
 */
@Serializable
data class DamageCommand(
    override val toId: Int,
    val attack: Double,
) : DefaultCommand() {
    override fun name(): String = "Damage"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        // Disable military base recovery by 1 turn
        playerData.playerInternalData.modifierData().combatModifierData
            .disableMilitaryBaseRecoveryByTime(1)

        val carrierDataMap: Map<Int, MutableCarrierData> = playerData.playerInternalData
            .popSystemData().carrierDataMap

        val carrierIdList: List<Int> = carrierDataMap.keys.sortedBy {
            carrierDataMap.getValue(it).allPopData.totalAdultPopulation()
        }

        // Use attack to destroy carrier, until used up or no carrier left
        var attackAcc: Double = attack

        // Attack consume shield
        carrierIdList.forEach {
            val carrierData: MutableCarrierData = carrierDataMap.getValue(it)

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
