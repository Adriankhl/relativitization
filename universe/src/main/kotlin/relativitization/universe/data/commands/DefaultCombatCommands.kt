package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.utils.I18NString

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
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        // Disable military base recovery by 1 turn
        playerData.playerInternalData.modifierData().combatModifierData.disableMilitaryBaseRecoveryByTime(
            1
        )

        val carrierIdList: MutableList<Int> =
            playerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled()
                .toMutableList()

        // Use attack to destroy carrier, until used up or no carrier left
        var attackAcc: Double = attack

        // Attack consume shield
        carrierIdList.forEach {

            val carrierData: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    it
                )

            val shield: Double = carrierData.allPopData.soldierPopData.militaryBaseData.shield

            if (shield > attackAcc) {
                carrierData.allPopData.soldierPopData.militaryBaseData.shield -= attackAcc
                attackAcc = 0.0
            } else {
                if (carrierData.carrierType == CarrierType.SPACESHIP) {
                    // Destroy this carrier
                    playerData.playerInternalData.popSystemData().carrierDataMap.remove(it)
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
                playerData.changeDirectLeaderId(listOf(fromId))
            }
        }
    }
}
