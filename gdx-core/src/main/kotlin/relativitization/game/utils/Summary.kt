package relativitization.game.utils

import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.popsystem.CarrierData

object Summary {
    fun compute(
        thisPlayer: PlayerData,
        otherPlayerList: List<PlayerData> = listOf(),
    ): CarrierListSummary {

        val carrierList: List<CarrierData> = thisPlayer.playerInternalData.popSystemData().carrierDataMap.values +
                otherPlayerList.flatMap { it.playerInternalData.popSystemData().carrierDataMap.values }

        val totalAttack: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.attack
        }

        val totalShield: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.shield
        }

        return CarrierListSummary(
            playerId = thisPlayer.playerId,
            numCarrier = carrierList.size,
            totalAttack = totalAttack,
            totalShield = totalShield,
        )
    }
}

data class CarrierListSummary(
    val playerId: Int,
    val numCarrier: Int,
    val totalAttack: Double,
    val totalShield: Double,
)