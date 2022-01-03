package relativitization.universe.ai.defaults.consideration.building

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData

/**
 * Check if there is no self fuel factory and no stellar pop system
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoSelfFuelFactoryAndNoStarConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasSelfFuelFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.any {
                    it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                }
            }

        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        return if (hasSelfFuelFactory || hasStellarSystem) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if there is fewer than or equal to one fuel factory
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class OneSelfFuelFactoryConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val numSelfFuelFactory: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.fold(0) { acc, carrier ->
                acc + carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter {
                    it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                }.size
            }

        return if (numSelfFuelFactory > 1) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if this fuel factory uses outdated technology
 *
 * @property carrierId the id of the carrier with this factory
 * @property fuelFactoryId the id of the fuel factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 */
class OutdatedFuelFactoryConsideration(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelFactory: MutableFuelFactoryData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
            .allPopData.labourerPopData.fuelFactoryMap.getValue(fuelFactoryId)

        val idealFuelFactory: MutableFuelFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .playerScienceApplicationData.idealFuelFactory

        // Compute the ratio of output over the employee, which should be affected by tech level
        val ratio: Double = fuelFactory.fuelFactoryInternalData.maxOutputAmount /
                fuelFactory.fuelFactoryInternalData.maxNumEmployee
        val idealRatio: Double = idealFuelFactory.maxOutputAmount / idealFuelFactory.maxNumEmployee

        return if (ratio == idealRatio) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            if (ratio > 0.0) {
                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = (idealRatio - ratio) / ratio
                )
            } else {
                // If ratio equals to 0, you should build a new factory
                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = (idealRatio - ratio) / 1E-10
                )
            }
        }
    }
}

/**
 * Check if self fuel factory is sufficient
 *
 * @property carrierId the id of the carrier to consider
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientSelfFuelFactoryConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        val selfFuelFactoryList: List<MutableFuelFactoryData> =
            carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter {
                it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }

        val totalMaxEmployee: Double = selfFuelFactoryList.fold(0.0){ acc, fuelFactory ->
            acc + fuelFactory.fuelFactoryInternalData.maxNumEmployee * fuelFactory.numBuilding
        }

        val totalLabourerPopulation: Double =
            carrier.allPopData.labourerPopData.commonPopData.adultPopulation

        // Sufficient if fuel factory position is more than half of the population
        return if (totalMaxEmployee >= totalLabourerPopulation * 0.5) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}

/**
 * Check if there isn't any self resource factory of this resource type
 *
 * @property resourceType the resource type to check
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoSelfResourceFactoryConsideration(
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasSelfResourceFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any { carrier ->
                carrier.allPopData.labourerPopData.resourceFactoryMap.values.any {
                    val isThisResource: Boolean =
                        it.resourceFactoryInternalData.outputResource == resourceType
                    val isSelf: Boolean =
                        it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    isThisResource && isSelf
                }
            }

        return if (hasSelfResourceFactory) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if there isn't any self resource factory of this resource type and has at least a star
 *
 * @property resourceType the resource type to check
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoSelfResourceFactoryAndHasStarConsideration(
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasSelfResourceFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any { carrier ->
                carrier.allPopData.labourerPopData.resourceFactoryMap.values.any {
                    val isThisResource: Boolean =
                        it.resourceFactoryInternalData.outputResource == resourceType
                    val isSelf: Boolean =
                        it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    isThisResource && isSelf
                }
            }

        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        return if (hasSelfResourceFactory || !hasStellarSystem) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}