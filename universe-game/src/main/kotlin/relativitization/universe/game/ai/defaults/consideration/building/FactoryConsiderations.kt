package relativitization.universe.game.ai.defaults.consideration.building

import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.PlayerData
import relativitization.universe.game.data.components.PopSystemData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.CarrierData
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.FuelFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableInputResourceData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData

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
) : DualUtilityConsideration() {
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
            DualUtilityDataFactory.noImpact()
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
class OutdatedSelfFuelFactoryConsideration(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
) : DualUtilityConsideration() {
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
        val outputRatio: Double = fuelFactory.fuelFactoryInternalData.maxOutputAmountPerEmployee

        val idealOutputRatio: Double = idealFuelFactory.maxOutputAmountPerEmployee

        return if (outputRatio >= idealOutputRatio) {
            DualUtilityDataFactory.noImpact()
        } else {
            if (outputRatio > 0.0) {
                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = (idealOutputRatio - outputRatio) / outputRatio
                )
            } else {
                // If ratio equals to 0, set a high bonus
                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = 10.0
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
class SufficientSelfFuelFactoryAtCarrierConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        return if (isTrue(planDataAtPlayer, carrierId)) {
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

    companion object {
        fun isTrue(
            planDataAtPlayer: PlanDataAtPlayer,
            carrierId: Int,
        ): Boolean {
            val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

            val selfFuelFactoryList: List<MutableFuelFactoryData> =
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter {
                    it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                }

            val totalMaxEmployee: Double = selfFuelFactoryList.fold(0.0) { acc, fuelFactory ->
                acc + fuelFactory.maxNumEmployee
            }

            val totalLabourerPopulation: Double =
                carrier.allPopData.labourerPopData.commonPopData.adultPopulation

            // Sufficient if fuel factory position is more than half of the labourer population
            return totalMaxEmployee >= totalLabourerPopulation * 0.5
        }
    }
}

/**
 * Check if employment is sufficient after removing this factory
 *
 * @property carrierId the id of the carrier to consider
 * @property fuelFactoryId the id of the fuel factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientFuelFactoryAtCarrierAfterRemoveConsideration(
    private val carrierId: Int,
    private val fuelFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        val selfFuelFactoryAfterRemoveList: List<MutableFuelFactoryData> =
            carrier.allPopData.labourerPopData.fuelFactoryMap.filter { (id, factoryData) ->
                (factoryData.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId) &&
                        (fuelFactoryId != id)
            }.values.toList()

        val totalMaxEmployee: Double =
            selfFuelFactoryAfterRemoveList.fold(0.0) { acc, fuelFactory ->
                acc + fuelFactory.maxNumEmployee
            }

        val totalLabourerPopulation: Double =
            carrier.allPopData.labourerPopData.commonPopData.adultPopulation

        // Sufficient if fuel factory position is more than half of the labourer population
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
 * Check if there is too many self fuel factory compare to all self factory
 *
 * @property carrierId the id of the carrier to consider
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class TooManySelfFuelFactoryAtCarrierConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
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

        val selfResourceFactoryList: List<MutableResourceFactoryData> =
            carrier.allPopData.labourerPopData.resourceFactoryMap.values.filter {
                it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }

        val selfFuelFactoryMaxEmployee: Double = selfFuelFactoryList.fold(0.0) { acc, fuelFactory ->
            acc + fuelFactory.maxNumEmployee
        }

        val selfResourceFactoryMaxEmployee: Double =
            selfResourceFactoryList.fold(0.0) { acc, resourceFactory ->
                acc + resourceFactory.maxNumEmployee
            }

        // Too many if self fuel employee is more than all others' employee
        return if (selfFuelFactoryMaxEmployee > selfResourceFactoryMaxEmployee) {
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
) : DualUtilityConsideration() {
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
            DualUtilityDataFactory.noImpact()
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
) : DualUtilityConsideration() {
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
            DualUtilityDataFactory.noImpact()
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if this resource factory uses outdated technology
 *
 * @property carrierId the id of the carrier with this factory
 * @property resourceFactoryId the id of the fuel factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 */
class OutdatedResourceFactoryConsideration(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val resourceFactory: MutableResourceFactoryData =
            planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
                .allPopData.labourerPopData.resourceFactoryMap.getValue(resourceFactoryId)

        val idealResourceFactory: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .playerScienceApplicationData.getIdealResourceFactory(
                resourceFactory.resourceFactoryInternalData.outputResource
            )

        // Compute the ratio of output over the employee, which should be affected by tech level
        val outputRatio: Double =
            resourceFactory.resourceFactoryInternalData.maxOutputAmountPerEmployee
        val idealOutputRatio: Double = idealResourceFactory.maxOutputAmountPerEmployee

        val qualityMag: Double =
            resourceFactory.resourceFactoryInternalData.maxOutputResourceQualityData.mag()
        val idealQualityMag: Double = idealResourceFactory.maxOutputResourceQualityData.mag()

        return if ((outputRatio == idealOutputRatio) && (qualityMag == idealQualityMag)) {
            DualUtilityDataFactory.noImpact()
        } else {
            if ((outputRatio > 0.0) && (qualityMag > 0.0)) {
                val outputRatioBonus: Double = (idealOutputRatio - outputRatio) / outputRatio
                val qualityBonus: Double = (idealQualityMag - qualityMag) / qualityMag

                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = outputRatioBonus + qualityBonus
                )
            } else {
                // If ratio equals to 0, set a high bonus
                DualUtilityData(
                    rank = rankIfTrue,
                    multiplier = multiplierIfTrue,
                    bonus = 10.0
                )
            }
        }
    }
}

/**
 * Check if self resource factory is sufficient
 *
 * @property carrierId the id of the carrier to consider
 * @property resourceType the type of resource of the factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientSelfResourceFactoryAtCarrierConsideration(
    private val carrierId: Int,
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        return if (isTrue(planDataAtPlayer, carrierId, resourceType)) {
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

    companion object {
        fun isTrue(
            planDataAtPlayer: PlanDataAtPlayer,
            carrierId: Int,
            resourceType: ResourceType,
        ): Boolean {
            val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

            val selfResourceFactoryList: List<MutableResourceFactoryData> =
                carrier.allPopData.labourerPopData.resourceFactoryMap.values.filter {
                    val isThisResource: Boolean =
                        it.resourceFactoryInternalData.outputResource == resourceType
                    val isSelf: Boolean =
                        it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                    isThisResource && isSelf
                }

            val totalMaxEmployee: Double =
                selfResourceFactoryList.fold(0.0) { acc, resourceFactory ->
                    acc + resourceFactory.maxNumEmployee
                }

            val totalLabourerPopulation: Double =
                carrier.allPopData.labourerPopData.commonPopData.adultPopulation

            // Sufficient if resource factory position is more than 0.1 of the labourer population
            return totalMaxEmployee >= totalLabourerPopulation * 0.1
        }
    }
}

/**
 * Check if self resource factory is sufficient after removing a resource factory
 *
 * @property carrierId the id of the carrier to consider
 * @property resourceFactoryId the id of the resource factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientSelfResourceFactoryAfterRemoveConsideration(
    private val carrierId: Int,
    private val resourceFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        val resourceType: ResourceType =
            carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
                resourceFactoryId
            ).resourceFactoryInternalData.outputResource

        val selfResourceFactoryList: List<MutableResourceFactoryData> =
            carrier.allPopData.labourerPopData.resourceFactoryMap.filter { (id, factoryData) ->
                val isThisResource: Boolean =
                    factoryData.resourceFactoryInternalData.outputResource == resourceType
                val isSelf: Boolean =
                    factoryData.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
                val isNotThisFactory: Boolean = id != resourceFactoryId
                isThisResource && isSelf && isNotThisFactory
            }.values.toList()

        val totalMaxEmployee: Double = selfResourceFactoryList.fold(0.0) { acc, resourceFactory ->
            acc + resourceFactory.maxNumEmployee
        }

        val totalLabourerPopulation: Double =
            carrier.allPopData.labourerPopData.commonPopData.adultPopulation

        // Sufficient if resource factory position is more than 0.1 of the labourer population
        return if (totalMaxEmployee >= totalLabourerPopulation * 0.1) {
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
 * Check if there is too many self resource factory compare to all self factory
 *
 * @property carrierId the id of the carrier to consider
 * @property resourceType the type of resource of the factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class TooManySelfResourceFactoryAtCarrierConsideration(
    private val carrierId: Int,
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
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

        val selfResourceFactoryList: List<MutableResourceFactoryData> =
            carrier.allPopData.labourerPopData.resourceFactoryMap.values.filter {
                it.ownerPlayerId == planDataAtPlayer.getCurrentMutablePlayerData().playerId
            }

        val (thisResourceFactoryList, otherResourceFactoryList) = selfResourceFactoryList.partition {
            it.resourceFactoryInternalData.outputResource == resourceType
        }

        val thisResourceMaxEmployee: Double =
            thisResourceFactoryList.fold(0.0) { acc, resourceFactory ->
                acc + resourceFactory.maxNumEmployee
            }

        val selfFuelFactoryMaxEmployee: Double = selfFuelFactoryList.fold(0.0) { acc, fuelFactory ->
            acc + fuelFactory.maxNumEmployee
        }

        val otherResourceFactoryMaxEmployee: Double =
            otherResourceFactoryList.fold(0.0) { acc, resourceFactory ->
                acc + resourceFactory.maxNumEmployee
            }

        // Too many if this resource employee is more than 0.1 of all others' employee
        return if (thisResourceMaxEmployee > (selfFuelFactoryMaxEmployee + otherResourceFactoryMaxEmployee) * 0.2) {
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
 * Check if there are sufficient employment position in all labourer
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientLabourerEmploymentConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val isSufficient: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.all { mutableCarrierData ->
                val popAmount: Double =
                    mutableCarrierData.allPopData.labourerPopData.commonPopData.adultPopulation
                val fuelFactoryEmploymentAmount: Double =
                    mutableCarrierData.allPopData.labourerPopData.fuelFactoryMap.values.fold(
                        0.0
                    ) { acc, mutableFuelFactoryData ->
                        acc + mutableFuelFactoryData.maxNumEmployee
                    }
                val resourceFactoryEmploymentAmount: Double =
                    mutableCarrierData.allPopData.labourerPopData.resourceFactoryMap.values.fold(
                        0.0
                    ) { acc, mutableResourceFactoryData ->
                        acc + mutableResourceFactoryData.maxNumEmployee
                    }

                fuelFactoryEmploymentAmount + resourceFactoryEmploymentAmount >= popAmount
            }

        return if (isSufficient) {
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
 * Check if a new foreign fuel factory has lower cost than local factory
 *
 * @property otherPlayerId the id of the player with this factory
 * @property otherCarrierId the id of the carrier with this factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class NewForeignFuelFactoryLowerCostConsideration(
    private val otherPlayerId: Int,
    private val otherCarrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            otherPlayerId,
            planDataAtPlayer
        )

        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        val otherPopSystemData: PopSystemData = otherPlayerData.playerInternalData.popSystemData()

        val otherCarrier: CarrierData = otherPopSystemData.carrierDataMap.getValue(otherCarrierId)

        val otherSalaryPerEmployee: Double = otherCarrier.allPopData.labourerPopData.commonPopData
            .salaryPerEmployee(otherPopSystemData.generalPopSystemData)

        val otherIncomeTax: Double = otherPlayerData.playerInternalData.economyData().taxData
            .taxRateData.incomeTax.getIncomeTax(otherSalaryPerEmployee)

        val otherSalaryWithTax: Double = otherSalaryPerEmployee * (1.0 + otherIncomeTax)

        // cost per output, divided by 2 fuel remain fraction since the fuel has to send back and
        // forth
        val otherCost: Double = otherSalaryWithTax / fuelRemainFraction / fuelRemainFraction

        val averageSelfSalary: Double = planState.averageSelfLabourerSalary(planDataAtPlayer)

        val selfIncomeTax: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().taxData.taxRateData.incomeTax.getIncomeTax(
                averageSelfSalary
            )

        val selfCost: Double = averageSelfSalary * (1.0 + selfIncomeTax)

        return if (otherCost < selfCost) {
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
 * Check if a specific fuel factory has lower cost then a new one
 *
 * @property otherPlayerId the id of the player with this factory
 * @property otherCarrierId the id of the carrier with this factory
 * @property fuelFactoryId the id of the fuel factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class ForeignFuelFactoryLowerCostConsideration(
    private val otherPlayerId: Int,
    private val otherCarrierId: Int,
    private val fuelFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            otherPlayerId,
            planDataAtPlayer
        )

        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        val otherPopSystemData: PopSystemData = otherPlayerData.playerInternalData.popSystemData()

        val otherCarrier: CarrierData = otherPopSystemData.carrierDataMap.getValue(otherCarrierId)

        val otherFuelFactory: FuelFactoryData = otherCarrier.allPopData.labourerPopData
            .fuelFactoryMap.getValue(fuelFactoryId)

        val otherOutputRatio: Double =
            otherFuelFactory.fuelFactoryInternalData.maxOutputAmountPerEmployee

        val otherSalaryPerEmployee: Double = otherCarrier.allPopData.labourerPopData.commonPopData
            .salaryPerEmployee(otherPopSystemData.generalPopSystemData)

        val otherIncomeTax: Double = otherPlayerData.playerInternalData.economyData().taxData
            .taxRateData.incomeTax.getIncomeTax(otherSalaryPerEmployee)

        val otherSalaryWithTax: Double = otherSalaryPerEmployee * (1.0 + otherIncomeTax)

        // cost per output, divided by 2 fuel remain fraction since the fuel has to send back and
        // forth
        val otherCostPerOutput: Double = otherSalaryWithTax / fuelRemainFraction /
                fuelRemainFraction / otherOutputRatio

        val selfIdealFuelFactory: MutableFuelFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .playerScienceApplicationData.idealFuelFactory

        val selfOutputRatio: Double = selfIdealFuelFactory.maxOutputAmountPerEmployee

        val averageSelfSalary: Double = planState.averageSelfLabourerSalary(planDataAtPlayer)

        val selfIncomeTax: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().taxData.taxRateData.incomeTax.getIncomeTax(
                averageSelfSalary
            )

        val selfCostPerOutput: Double = averageSelfSalary * (1.0 + selfIncomeTax) / selfOutputRatio

        return if (otherCostPerOutput < selfCostPerOutput) {
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
 * Check if a new foreign resource factory has lower cost than local factory
 *
 * @property otherPlayerId the id of the player with this factory
 * @property otherCarrierId the id of the carrier with this factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class NewForeignResourceFactoryLowerCostConsideration(
    private val otherPlayerId: Int,
    private val otherCarrierId: Int,
    private val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            otherPlayerId,
            planDataAtPlayer,
        )

        val resourceRemainFraction: Double = planState.resourceRemainFraction(
            otherPlayerId,
            planDataAtPlayer,
        )

        val selfIdealResourceFactory: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .playerScienceApplicationData.getIdealResourceFactory(resourceType)

        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        val otherPopSystemData: PopSystemData = otherPlayerData.playerInternalData.popSystemData()

        val otherCarrier: CarrierData = otherPopSystemData.carrierDataMap.getValue(otherCarrierId)

        val otherSalaryPerEmployee: Double = otherCarrier.allPopData.labourerPopData.commonPopData
            .salaryPerEmployee(otherPopSystemData.generalPopSystemData)

        val otherIncomeTax: Double = otherPlayerData.playerInternalData.economyData().taxData
            .taxRateData.incomeTax.getIncomeTax(otherSalaryPerEmployee)

        val otherSalaryWithTax: Double = otherSalaryPerEmployee * (1.0 + otherIncomeTax)

        val otherInputCost: Double = selfIdealResourceFactory.inputResourceMap.keys.fold(
            0.0
        ) { acc, inputResourceType ->
            val inputResourceData: MutableInputResourceData =
                selfIdealResourceFactory.inputResourceMap.getValue(inputResourceType)

            // Estimate the quality class with amount=1.0
            val qualityClass: ResourceQualityClass = otherPlayerData.playerInternalData
                .economyData().resourceData.tradeQualityClass(
                    resourceType = inputResourceType,
                    amount = 1.0,
                    targetQuality = inputResourceData.qualityData.toResourceQualityData(),
                    budget = 1E100,
                    preferHighQualityClass = false
                )
            val price: Double = inputResourceData.amountPerOutput * otherPlayerData.playerInternalData
                .economyData().resourceData.getResourcePrice(inputResourceType, qualityClass)

            acc + price
        }

        // cost per output
        val otherCost: Double = (otherSalaryWithTax + otherInputCost +
                selfIdealResourceFactory.fuelRestMassConsumptionRatePerEmployee) /
                fuelRemainFraction / resourceRemainFraction

        val averageSelfSalary: Double = planState.averageSelfLabourerSalary(planDataAtPlayer)

        val selfIncomeTax: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().taxData.taxRateData.incomeTax.getIncomeTax(
                averageSelfSalary
            )

        // Estimate cost if input resource from self
        val selfInputCost: Double = selfIdealResourceFactory.inputResourceMap.keys.fold(
            0.0
        ) { acc, inputResourceType ->
            val inputResourceData: MutableInputResourceData =
                selfIdealResourceFactory.inputResourceMap.getValue(inputResourceType)

            // Estimate the quality class with amount 1.0
            val qualityClass: ResourceQualityClass = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.economyData().resourceData.productionQualityClass(
                    resourceType = inputResourceType,
                    amount = 1.0,
                    targetQuality = inputResourceData.qualityData,
                    preferHighQualityClass = false,
                )
            val price: Double = inputResourceData.amountPerOutput * planDataAtPlayer
                .getCurrentMutablePlayerData().playerInternalData.economyData().resourceData
                .getResourcePrice(inputResourceType, qualityClass)

            acc + price
        }

        val selfCost: Double = averageSelfSalary * (1.0 + selfIncomeTax) + selfInputCost +
                selfIdealResourceFactory.fuelRestMassConsumptionRatePerEmployee

        return if (otherCost < selfCost) {
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
 * Check if a new foreign resource factory has lower cost than local factory
 *
 * @property otherPlayerId the id of the player with this factory
 * @property otherCarrierId the id of the carrier with this factory
 * @property resourceFactoryId the id of the resource factory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class ForeignResourceFactoryLowerCostConsideration(
    private val otherPlayerId: Int,
    private val otherCarrierId: Int,
    private val resourceFactoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelRemainFraction: Double = planState.fuelRemainFraction(
            otherPlayerId,
            planDataAtPlayer,
        )

        val resourceRemainFraction: Double = planState.resourceRemainFraction(
            otherPlayerId,
            planDataAtPlayer,
        )

        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(otherPlayerId)

        val otherPopSystemData: PopSystemData = otherPlayerData.playerInternalData.popSystemData()

        val otherCarrier: CarrierData = otherPopSystemData.carrierDataMap.getValue(otherCarrierId)

        val otherResourceFactoryData: ResourceFactoryData = otherCarrier.allPopData.labourerPopData
            .resourceFactoryMap.getValue(resourceFactoryId)

        val outPutResourceType: ResourceType =
            otherResourceFactoryData.resourceFactoryInternalData.outputResource

        val otherSalaryPerEmployee: Double = otherCarrier.allPopData.labourerPopData.commonPopData
            .salaryPerEmployee(otherPopSystemData.generalPopSystemData)

        val otherIncomeTax: Double = otherPlayerData.playerInternalData.economyData().taxData
            .taxRateData.incomeTax.getIncomeTax(otherSalaryPerEmployee)

        val otherSalaryWithTax: Double = otherSalaryPerEmployee * (1.0 + otherIncomeTax)

        val otherOutputRatio: Double =
            otherResourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee

        val otherInputCost: Double = otherResourceFactoryData.resourceFactoryInternalData
            .inputResourceMap.keys.fold(0.0) { acc, inputResourceType ->
                val inputResourceData: InputResourceData = otherResourceFactoryData
                    .resourceFactoryInternalData.inputResourceMap.getValue(inputResourceType)

                // Estimate the quality class with amount=1.0
                val qualityClass: ResourceQualityClass = otherPlayerData.playerInternalData
                    .economyData().resourceData.tradeQualityClass(
                        resourceType = inputResourceType,
                        amount = 1.0,
                        targetQuality = inputResourceData.qualityData,
                        budget = 1E100,
                        preferHighQualityClass = false
                    )
                val price: Double = inputResourceData.amountPerOutput * otherPlayerData.playerInternalData
                    .economyData().resourceData.getResourcePrice(inputResourceType, qualityClass)

                acc + price
            }

        // cost per output
        val otherCost: Double = (otherSalaryWithTax + otherInputCost + otherResourceFactoryData
            .resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee) /
                otherOutputRatio / fuelRemainFraction / resourceRemainFraction

        val selfIdealResourceFactory: MutableResourceFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .playerScienceApplicationData.getIdealResourceFactory(outPutResourceType)

        val averageSelfSalary: Double = planState.averageSelfLabourerSalary(planDataAtPlayer)

        val selfIncomeTax: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().taxData.taxRateData.incomeTax.getIncomeTax(
                averageSelfSalary
            )

        val selfOutputRatio: Double = selfIdealResourceFactory.maxOutputAmountPerEmployee

        // Estimate cost if input resource from self
        val selfInputCost: Double = selfIdealResourceFactory.inputResourceMap.keys.fold(
            0.0
        ) { acc, inputResourceType ->
            val inputResourceData: MutableInputResourceData =
                selfIdealResourceFactory.inputResourceMap.getValue(inputResourceType)

            // Estimate the quality class with amount 1.0
            val qualityClass: ResourceQualityClass = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.economyData().resourceData.productionQualityClass(
                    resourceType = inputResourceType,
                    amount = 1.0,
                    targetQuality = inputResourceData.qualityData,
                    preferHighQualityClass = false,
                )
            val price: Double = inputResourceData.amountPerOutput * planDataAtPlayer
                .getCurrentMutablePlayerData().playerInternalData.economyData().resourceData
                .getResourcePrice(inputResourceType, qualityClass)

            acc + price
        }

        val selfCost: Double = (averageSelfSalary * (1.0 + selfIncomeTax) + selfInputCost +
                selfIdealResourceFactory.fuelRestMassConsumptionRatePerEmployee) / selfOutputRatio

        return if (otherCost < selfCost) {
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