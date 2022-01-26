package relativitization.game.utils

import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

object Summary {
    fun compute(
        thisPlayer: PlayerData,
        otherPlayerList: List<PlayerData> = listOf(),
    ): PlayerSummary {

        val carrierList: List<CarrierData> = thisPlayer.playerInternalData.popSystemData().carrierDataMap.values +
                otherPlayerList.flatMap { it.playerInternalData.popSystemData().carrierDataMap.values }

        val totalAttack: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.attack
        }

        val totalShield: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.shield
        }

        val totalFuelConsumption: Double = carrierList.sumOf { carrierData ->
            val totalSalary: Double = PopType.values().sumOf { popType ->
                val commonPopData: CommonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee * commonPopData.adultPopulation * (1.0 - commonPopData.unemploymentRate)
            }
            val totalResourceFactoryConsumption: Double = carrierData.allPopData.labourerPopData.resourceFactoryMap
                .values.sumOf { resourceFactory ->
                    resourceFactory.resourceFactoryInternalData.fuelRestMassConsumptionRate *
                            resourceFactory.numBuilding * resourceFactory.employeeFraction()
                }
            totalSalary + totalResourceFactoryConsumption
        }


        val resourceConsumptionMap: Map<ResourceType, Double> = ResourceType.values().associateWith { resourceType ->
            0.0
        }

        return PlayerSummary(
            playerId = thisPlayer.playerId,
            numCarrier = carrierList.size,
            totalAttack = totalAttack,
            totalShield = totalShield,
            totalFuelConsumption = totalFuelConsumption,
        )
    }
}

data class PlayerSummary(
    val playerId: Int,
    val numCarrier: Int,
    val totalAttack: Double,
    val totalShield: Double,
    val totalFuelConsumption: Double,
)