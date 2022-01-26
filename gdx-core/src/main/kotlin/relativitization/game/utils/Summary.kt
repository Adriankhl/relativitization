package relativitization.game.utils

import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.mechanisms.defaults.dilated.production.BaseStellarFuelProduction

object Summary {
    fun compute(
        thisPlayer: PlayerData,
        otherPlayerList: List<PlayerData> = listOf(),
    ): PlayerSummary {
        val allPlayerId: List<Int> = listOf(thisPlayer.playerId) + otherPlayerList.map { it.playerId }

        val carrierList: List<CarrierData> = thisPlayer.playerInternalData.popSystemData().carrierDataMap.values +
                otherPlayerList.flatMap { it.playerInternalData.popSystemData().carrierDataMap.values }

        val totalAttack: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.attack
        }

        val totalShield: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.shield
        }

        val totalFuelDemand: Double = carrierList.sumOf { carrierData ->
            val totalSalary: Double = PopType.values().sumOf { popType ->
                val commonPopData: CommonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee * commonPopData.adultPopulation * (1.0 - commonPopData.unemploymentRate)
            }
            val totalResourceFactoryConsumption: Double = carrierData.allPopData.labourerPopData.resourceFactoryMap
                .values.sumOf { resourceFactory ->
                    // only consider self resource factory
                    if (allPlayerId.contains(resourceFactory.ownerPlayerId)) {
                        resourceFactory.resourceFactoryInternalData.fuelRestMassConsumptionRate *
                                resourceFactory.numBuilding * resourceFactory.employeeFraction()
                    } else {
                        0.0
                    }
                }
            totalSalary + totalResourceFactoryConsumption
        }

        val totalFuelSupply: Double = carrierList.sumOf { carrierData ->
            val baseProduction: Double = BaseStellarFuelProduction.baseFuelProduction(
                carrierData.carrierType,
                carrierData.carrierInternalData.coreRestMass
            )

            val fuelFactoryProduction: Double = carrierData.allPopData.labourerPopData.fuelFactoryMap
                .values.sumOf { fuelFactory ->
                    // Only calculate fuel factory belongs to this group
                    if (allPlayerId.contains(fuelFactory.ownerPlayerId)) {
                        fuelFactory.lastOutputAmount
                    } else {
                        0.0
                    }
                }

            baseProduction + fuelFactoryProduction
        }


        val resourceConsumptionMap: Map<ResourceType, Double> = ResourceType.values().associateWith { resourceType ->
            0.0
        }

        return PlayerSummary(
            playerId = thisPlayer.playerId,
            numCarrier = carrierList.size,
            totalAttack = totalAttack,
            totalShield = totalShield,
            totalFuelDemand = totalFuelDemand,
            totalFuelSupply = totalFuelSupply,
        )
    }
}

data class PlayerSummary(
    val playerId: Int,
    val numCarrier: Int,
    val totalAttack: Double,
    val totalShield: Double,
    val totalFuelDemand: Double,
    val totalFuelSupply: Double,
)