package relativitization.game.utils

import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.ResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.mechanisms.defaults.dilated.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.defaults.dilated.production.EntertainmentProduction

object Summary {
    fun compute(
        thisPlayer: PlayerData,
        otherPlayerList: List<PlayerData> = listOf(),
    ): PlayerSummary {
        val allPlayerId: List<Int> = listOf(thisPlayer.playerId) + otherPlayerList.map { it.playerId }

        val carrierList: List<CarrierData> = thisPlayer.playerInternalData.popSystemData().carrierDataMap.values +
                otherPlayerList.flatMap { it.playerInternalData.popSystemData().carrierDataMap.values }

        val totalPopulation: Double = carrierList.sumOf {
            it.allPopData.totalAdultPopulation()
        }

        val totalAttack: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.attack
        }

        val totalShield: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.shield
        }

        val totalFuelDemand: Double = carrierList.sumOf { carrierData ->
            val totalSalary: Double = PopType.values().sumOf { popType ->
                val commonPopData: CommonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee * commonPopData.adultPopulation
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


        val totalResourceDemandMap: Map<ResourceType, Double> = ResourceType.values().associateWith { resourceType ->
            carrierList.sumOf { carrierData ->
                val popDesire: Double = PopType.values().sumOf { popType ->
                    carrierData.allPopData.getCommonPopData(popType).desireResourceMap.getOrDefault(
                        resourceType,
                        ResourceDesireData()
                    ).desireAmount
                }

                val resourceFactoryDesire: Double = carrierData.allPopData.labourerPopData.resourceFactoryMap
                    .values.sumOf { resourceFactory ->
                        resourceFactory.resourceFactoryInternalData.inputResourceMap.getOrDefault(
                            resourceType,
                            InputResourceData(),
                        ).amount * resourceFactory.numBuilding * resourceFactory.employeeFraction()
                    }

                val researchDesire: Double = if (resourceType == ResourceType.RESEARCH_EQUIPMENT) {
                    carrierData.allPopData.scholarPopData.instituteMap.values.sumOf { institute ->
                        institute.instituteInternalData.researchEquipmentPerTime
                    } + carrierData.allPopData.engineerPopData.laboratoryMap.values.sumOf { laboratory ->
                        laboratory.laboratoryInternalData.researchEquipmentPerTime
                    }
                } else {
                    0.0
                }

                popDesire + resourceFactoryDesire + researchDesire
            }
        }

        val totalResourceSupplyMap: Map<ResourceType, Double> = ResourceType.values().associateWith { resourceType ->
            carrierList.sumOf { carrierData ->
                val resourceFactoryOutput: Double = carrierData.allPopData.labourerPopData.resourceFactoryMap
                    .values.sumOf { resourceFactory ->
                        if (resourceFactory.resourceFactoryInternalData.outputResource == resourceType) {
                            resourceFactory.lastOutputAmount
                        } else {
                            0.0
                        }
                    }

                val entertainerOutput: Double = if (resourceType == ResourceType.ENTERTAINMENT) {
                    val commonPopData: CommonPopData =
                        carrierData.allPopData.entertainerPopData.commonPopData
                    EntertainmentProduction.computeEntertainmentAmount(
                        commonPopData.adultPopulation * (1.0 - commonPopData.unemploymentRate)
                    )
                } else {
                    0.0
                }

                resourceFactoryOutput + entertainerOutput
            }
        }

        val averageSatisfaction: Double = if (totalPopulation > 0.0) {
            carrierList.sumOf { carrierData ->
                PopType.values().sumOf { popType ->
                    val commonPopData: CommonPopData = carrierData.allPopData.getCommonPopData(popType)
                    commonPopData.satisfaction * commonPopData.adultPopulation
                }
            } / totalPopulation
        } else {
            0.0
        }

        return PlayerSummary(
            playerId = thisPlayer.playerId,
            numCarrier = carrierList.size,
            totalPopulation = totalPopulation,
            totalAttack = totalAttack,
            totalShield = totalShield,
            totalFuelDemand = totalFuelDemand,
            totalFuelSupply = totalFuelSupply,
            totalResourceDemandMap = totalResourceDemandMap,
            totalResourceSupplyMap = totalResourceSupplyMap,
            averageSatisfaction = averageSatisfaction,
        )
    }
}

data class PlayerSummary(
    val playerId: Int,
    val numCarrier: Int,
    val totalPopulation: Double,
    val totalAttack: Double,
    val totalShield: Double,
    val totalFuelDemand: Double,
    val totalFuelSupply: Double,
    val totalResourceDemandMap: Map<ResourceType, Double>,
    val totalResourceSupplyMap: Map<ResourceType, Double>,
    val averageSatisfaction: Double,
)