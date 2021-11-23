package relativitization.universe.mechanisms.default.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.default.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.algebra.Piecewise
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

object UpdateDesire : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Parameters
        val desireQualityUpdateFactor: Double = 0.2
        val desireQualityUpdateDiff: Double = 0.2
        val satisfactionMaxDecreaseFactor: Double = 0.2
        val satisfactionMaxIncreaseDelta: Double = 3.0


        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.values().forEach { popType ->
                val mutableCommonPopData: MutableCommonPopData =
                    carrier.allPopData.getCommonPopData(popType)

                val desireAmount: Double = computeDesireResourceAmount(
                    mutableCommonPopData = mutableCommonPopData
                )

                val desireResourceTypeList: List<ResourceType> = computeDesireResourceType(
                    popType
                )

                val desireResourceMap: Map<ResourceType, relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData> =
                    desireResourceTypeList.map {
                        val desireQualityData: MutableResourceQualityData =
                            computeDesireResourceQuality(
                                gamma = gamma,
                                mutableCommonPopData = mutableCommonPopData,
                                resourceType = it,
                                desireQualityUpdateFactor = desireQualityUpdateFactor,
                                desireQualityUpdateMinDiff = desireQualityUpdateDiff,
                            )

                        it to relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData(
                            desireAmount,
                            desireQualityData
                        )
                    }.toMap()

                updateSatisfaction(
                    gamma = gamma,
                    mutableCommonPopData = mutableCommonPopData,
                    desireResourceTypeList = desireResourceTypeList,
                    satisfactionMaxDecreaseFactor = satisfactionMaxDecreaseFactor,
                    satisfactionMaxIncreaseDelta = satisfactionMaxIncreaseDelta,
                )

                // Update desire and clear input
                mutableCommonPopData.desireResourceMap.clear()
                mutableCommonPopData.desireResourceMap.putAll(desireResourceMap)
                mutableCommonPopData.resourceInputMap.clear()
            }
        }

        return listOf()
    }

    /**
     * Compute desire resource type of pop
     *
     * @param popType the desire of this pop
     */
    fun computeDesireResourceType(
        popType: PopType
    ): List<ResourceType> {
        return when (popType) {
            PopType.LABOURER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.ENGINEER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.SCHOLAR -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.EDUCATOR -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.MEDIC -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
                ResourceType.MEDICINE,
            )
            PopType.SERVICE_WORKER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.ENTERTAINER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.SOLDIER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
                ResourceType.AMMUNITION,
            )
        }
    }

    /**
     * Compute the desire amount without the effect of time dilation
     */
    fun computeDesireResourceAmount(
        mutableCommonPopData: MutableCommonPopData
    ): Double {
        return mutableCommonPopData.adultPopulation
    }

    /**
     * Compute the new desire resource quality, adjusted by time dilation
     */
    fun computeDesireResourceQuality(
        gamma: Double,
        mutableCommonPopData: MutableCommonPopData,
        resourceType: ResourceType,
        desireQualityUpdateFactor: Double,
        desireQualityUpdateMinDiff: Double,
    ): MutableResourceQualityData {
        val originalDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
            mutableCommonPopData.desireResourceMap.getOrDefault(
                resourceType,
                relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
            )

        val inputDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
            mutableCommonPopData.resourceInputMap.getOrDefault(
                resourceType,
                relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
            )

        // If sufficient amount, get close to the input quality
        // else decrease the desire quality
        // Adjusted by time dilation
        return if (inputDesire.desireAmount > (originalDesire.desireAmount / gamma)) {
            originalDesire.desireQuality.changeTo(
                inputDesire.desireQuality,
                desireQualityUpdateFactor / gamma,
                desireQualityUpdateMinDiff / gamma,
            )
        } else {
            originalDesire.desireQuality.changeTo(
                MutableResourceQualityData(0.0, 0.0, 0.0),
                desireQualityUpdateFactor / gamma,
                desireQualityUpdateMinDiff / gamma,
            )
        }
    }

    /**
     * Compute satisfaction, adjusted by time dilation
     */
    fun updateSatisfaction(
        gamma: Double,
        mutableCommonPopData: MutableCommonPopData,
        desireResourceTypeList: List<ResourceType>,
        satisfactionMaxDecreaseFactor: Double,
        satisfactionMaxIncreaseDelta: Double,
    ) {
        val amountFractionList: List<Double> = desireResourceTypeList.map { resourceType ->
            val originalDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
                mutableCommonPopData.desireResourceMap.getOrDefault(
                    resourceType,
                    relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
                )

            val inputDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
                mutableCommonPopData.resourceInputMap.getOrDefault(
                    resourceType,
                    relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
                )

            if (originalDesire.desireAmount > 0.0) {
                inputDesire.desireAmount / (originalDesire.desireAmount / gamma)
            } else {
                1.0
            }
        }

        val qualityFractionList: List<Double> = desireResourceTypeList.map { resourceType ->
            val originalDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
                mutableCommonPopData.desireResourceMap.getOrDefault(
                    resourceType,
                    relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
                )

            val inputDesire: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData =
                mutableCommonPopData.resourceInputMap.getOrDefault(
                    resourceType,
                    relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
                )

            if (originalDesire.desireQuality.square() > 0.0) {
                inputDesire.desireQuality.mag() / originalDesire.desireQuality.mag()
            } else {
                1.0
            }
        }

        val originalSatisfaction: Double = mutableCommonPopData.satisfaction


        val amountFactor: Double = amountFractionList.fold(1.0) { acc, d ->
            acc * d
        }

        // Modify quality factor based on amount factor
        // If amount factor is small, the impact from quality should be small
        val qualityFactorWithoutMod: Double = qualityFractionList.fold(1.0) { acc, d ->
            acc * d
        }
        val qualityFactor: Double = if (amountFactor > 1.0) {
            qualityFactorWithoutMod
        } else {
            (qualityFactorWithoutMod - 1.0) * amountFactor + 1.0
        }

        val overallFactor: Double = amountFactor * qualityFactor

        // Compute the change of satisfaction by piecewise function
        val deltaSatisfaction: Double = Piecewise.quadLogistic(
            x = overallFactor,
            yMin = -satisfactionMaxDecreaseFactor * originalSatisfaction,
            yMax = satisfactionMaxIncreaseDelta,
            logisticSlope1 = 1.0
        )

        mutableCommonPopData.satisfaction += (deltaSatisfaction / gamma)
    }
}