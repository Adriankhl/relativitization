package relativitization.game.utils

import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.ResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.mechanisms.defaults.dilated.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.defaults.dilated.production.EntertainmentProduction

object Summary {
    /**
     * Compute a player summary
     *
     * @param thisPlayer this is the primary player of the summary
     * @param otherPlayerList also include these players in the summary
     */
    fun compute(
        thisPlayer: PlayerData,
        otherPlayerList: List<PlayerData> = listOf(),
    ): PlayerSummary {
        val allPlayerData: List<PlayerData> = listOf(thisPlayer) + otherPlayerList

        val allPlayerId: List<Int> = allPlayerData.map { it.playerId }

        val carrierList: List<CarrierData> = thisPlayer.playerInternalData.popSystemData()
            .carrierDataMap.values + otherPlayerList.flatMap {
            it.playerInternalData.popSystemData().carrierDataMap.values
            }

        val totalPopulation: Double = carrierList.sumOf {
            it.allPopData.totalAdultPopulation()
        }

        val totalAttack: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.attack
        }

        val totalShield: Double = carrierList.sumOf {
            it.allPopData.soldierPopData.militaryBaseData.shield
        }

        val totalSalary: Double = allPlayerData.sumOf {
            it.playerInternalData.popSystemData().totalSalary()
        }

        val totalFactoryConsumption: Double = carrierList.sumOf { carrierData ->
            val totalResourceFactoryConsumption: Double = carrierData.allPopData.labourerPopData
                .resourceFactoryMap.values.sumOf { resourceFactory ->
                    // only consider self resource factory
                    if (allPlayerId.contains(resourceFactory.ownerPlayerId)) {
                        resourceFactory.resourceFactoryInternalData
                            .fuelRestMassConsumptionRatePerEmployee * resourceFactory
                            .maxNumEmployee * resourceFactory.employeeFraction()
                    } else {
                        0.0
                    }
                }
            totalResourceFactoryConsumption
        }

        val totalFuelDemand: Double = totalSalary + totalFactoryConsumption

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
                        ).amountPerOutput * resourceFactory.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                                resourceFactory.maxNumEmployee *
                                resourceFactory.employeeFraction()
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
                        commonPopData.adultPopulation * commonPopData.employmentRate
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
            averageSatisfaction = averageSatisfaction,
            totalAttack = totalAttack,
            totalShield = totalShield,
            totalFuelDemand = totalFuelDemand,
            totalFuelSupply = totalFuelSupply,
            totalResourceDemandMap = totalResourceDemandMap,
            totalResourceSupplyMap = totalResourceSupplyMap,
        )
    }

    /**
     * Compute a summary from UniverseData3DAtPlayer
     *
     * @param thisPlayerId the id of the primary player of the summary
     * @param otherPlayerIdList the ids of the other player to include in the summary
     * @param universeData3DAtPlayer the data, should contain thisPlayerId, ids in otherPlayerIdList could be absent
     */
    fun computeFromUniverseData3DAtPlayer(
        thisPlayerId: Int,
        otherPlayerIdList: List<Int>,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): PlayerSummary {
        return if (universeData3DAtPlayer.playerDataMap.containsKey(thisPlayerId)) {
            val thisPlayerData: PlayerData = universeData3DAtPlayer.get(thisPlayerId)
            val otherPlayerDataList: List<PlayerData> = otherPlayerIdList.filter {
                universeData3DAtPlayer.playerDataMap.containsKey(it)
            }.map {
                universeData3DAtPlayer.get(it)
            }
            compute(thisPlayerData, otherPlayerDataList)
        } else {
            PlayerSummary()
        }
    }
}

data class PlayerSummary(
    val playerId: Int = -1,
    val numCarrier: Int = 0,
    val totalPopulation: Double = 0.0,
    val averageSatisfaction: Double = 0.0,
    val totalAttack: Double = 0.0,
    val totalShield: Double = 0.0,
    val totalFuelDemand: Double = 0.0,
    val totalFuelSupply: Double = 0.0,
    val totalResourceDemandMap: Map<ResourceType, Double> = ResourceType.values().associateWith { 0.0 },
    val totalResourceSupplyMap: Map<ResourceType, Double> = ResourceType.values().associateWith { 0.0 },
)