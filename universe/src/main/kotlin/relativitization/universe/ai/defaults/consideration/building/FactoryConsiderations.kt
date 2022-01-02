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
 * Check if there is no fuel factory and no stellar pop system
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoFuelFactoryAndNoStarConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasFuelFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.allPopData.labourerPopData.fuelFactoryMap.isNotEmpty()
            }

        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        return if (hasFuelFactory || hasStellarSystem) {
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
class OneFuelFactoryConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val numFuelFactory: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.fold(0) { acc, carrier ->
                acc + carrier.allPopData.labourerPopData.fuelFactoryMap.size
            }

        return if (numFuelFactory > 1) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if this fuel factory use outdated technology
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
 * Check if there is sufficient fuel factory
 *
 * @property carrierId the id of the carrier to consider
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientFuelFactoryConsideration(
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

        val fuelFactoryMap: Map<Int, MutableFuelFactoryData> =
            carrier.allPopData.labourerPopData.fuelFactoryMap

        val totalMaxEmployee: Double = fuelFactoryMap.values.fold(0.0){ acc, fuelFactory ->
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
 * Check if there isn't any resource factory of this resource type
 *
 * @property resourceType the resource type to check
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoResourceFactoryConsideration(
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasResourceFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.allPopData.labourerPopData.resourceFactoryMap.values.any {
                    it.resourceFactoryInternalData.outputResource == resourceType
                }
            }

        return if (hasResourceFactory) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if there isn't any resource factory of this resource type and has at least a star
 *
 * @property resourceType the resource type to check
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoResourceFactoryAndHasStarConsideration(
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasResourceFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.allPopData.labourerPopData.resourceFactoryMap.values.any {
                    it.resourceFactoryInternalData.outputResource == resourceType
                }
            }

        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        return if (hasResourceFactory || !hasStellarSystem) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}