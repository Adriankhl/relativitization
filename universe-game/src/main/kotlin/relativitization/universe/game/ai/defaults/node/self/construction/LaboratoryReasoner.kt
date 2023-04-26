package relativitization.universe.game.ai.defaults.node.self.construction

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.maths.physics.Double2D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.game.ai.defaults.consideration.building.KnownAppliedProjectInRangeConsideration
import relativitization.universe.game.ai.defaults.consideration.building.NoLaboratoryAtCarrierConsideration
import relativitization.universe.game.ai.defaults.consideration.building.OnlyOneLaboratoryConsideration
import relativitization.universe.game.ai.defaults.consideration.building.SufficientLaboratoryConsideration
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.commands.BuildLaboratoryCommand
import relativitization.universe.game.data.commands.RemoveLaboratoryCommand
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getTotalResourceAmount
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.numCarrier
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

class LaboratoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            RemoveLaboratoryReasoner(random),
            NewLaboratoryReasoner(random),
        )
    }
}

/**
 * Consider building new laboratory at all carrier
 */
class NewLaboratoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewLaboratoryAtCarrierReasoner(it, random)
        }
}

/**
 * Consider building new laboratory at a carrier
 */
class NewLaboratoryAtCarrierReasoner(
    private val carrierId: Int,
    private val random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            NewLaboratoryAtCarrierOption(
                carrierId = carrierId,
                random = random,
            ),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            )
        )
    }
}

/**
 * Option to build new laboratory
 */
class NewLaboratoryAtCarrierOption(
    private val carrierId: Int,
    private val random: Random,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            NoLaboratoryAtCarrierConsideration(
                carrierId = carrierId,
                rankIfTrue = 5,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0
            ),
            SufficientLaboratoryConsideration(
                carrierId = carrierId,
                rankIfTrue = 1,
                multiplierIfTrue = 0.2,
                bonusIfTrue = 0.1,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Determine number of new laboratory randomly
        val numNewLaboratory: Int = random.nextInt(1, 6)

        // Number of carrier in Double
        val numCarrier: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().numCarrier()

        val knownAppliedProjectList: List<AppliedResearchProjectData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .knownAppliedResearchProjectList

        // Compute the total research equipment available and need
        val totalResearchEquipment: Double = ResourceQualityClass.values().fold(
            0.0
        ) { acc, resourceQualityClass ->
            acc + planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.economyData().resourceData.getTotalResourceAmount(
                    ResourceType.RESEARCH_EQUIPMENT,
                    resourceQualityClass
                )
        }

        val researchEquipmentNeedPerTime: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.fold(
                0.0
            ) { acc, mutableCarrierData ->
                acc + mutableCarrierData.allPopData.scholarPopData.instituteMap.values.sumOf {
                    it.instituteInternalData.researchEquipmentPerTime
                } + mutableCarrierData.allPopData.engineerPopData.laboratoryMap.values.sumOf {
                    it.laboratoryInternalData.researchEquipmentPerTime
                }
            }

        // Determine the amount of research equipment per new laboratory
        val targetResearchEquipmentPerTime: Double = (totalResearchEquipment -
                researchEquipmentNeedPerTime) * 0.1 / numCarrier / numNewLaboratory

        // Determine the employee per new laboratory
        val targetMaxEmployee: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .engineerPopData.commonPopData.adultPopulation * 0.1 / numNewLaboratory

        (1..numNewLaboratory).forEach { _ ->
            val laboratoryList: List<MutableLaboratoryData> = planDataAtPlayer
                .getCurrentMutablePlayerData().playerInternalData.popSystemData().carrierDataMap
                .getValue(carrierId).allPopData.engineerPopData.laboratoryMap.values.toList()

            // Filter known project which is not in range of any laboratory
            val outOfRangeAppliedProject: List<AppliedResearchProjectData> =
                knownAppliedProjectList.filter { appliedProject ->
                    laboratoryList.all { laboratory ->
                        Intervals.distance(
                            laboratory.laboratoryInternalData.xCor,
                            laboratory.laboratoryInternalData.yCor,
                            appliedProject.xCor,
                            appliedProject.yCor
                        ) <= laboratory.laboratoryInternalData.range
                    }
                }

            val newCor: Double2D = if (outOfRangeAppliedProject.isNotEmpty()) {
                // Center at out-of-range project is it is not empty
                val targetProject: AppliedResearchProjectData = outOfRangeAppliedProject.first()
                Double2D(targetProject.xCor, targetProject.yCor)
            } else {
                val minLaboratoryX: Double = laboratoryList.minOfOrNull {
                    it.laboratoryInternalData.xCor - it.laboratoryInternalData.range
                } ?: 0.0
                val minLaboratoryY: Double = laboratoryList.minOfOrNull {
                    it.laboratoryInternalData.yCor - it.laboratoryInternalData.range
                } ?: 0.0
                val maxLaboratoryX: Double = laboratoryList.maxOfOrNull {
                    it.laboratoryInternalData.xCor + it.laboratoryInternalData.range
                } ?: 0.0
                val maxLaboratoryY: Double = laboratoryList.maxOfOrNull {
                    it.laboratoryInternalData.yCor + it.laboratoryInternalData.range
                } ?: 0.0

                // Randomly determine the knowledge coordinate of the new laboratory
                // Expand slightly from the existing laboratories
                Double2D(
                    random.nextDouble(minLaboratoryX - 1.0, maxLaboratoryX + 1.0),
                    random.nextDouble(minLaboratoryY - 1.0, maxLaboratoryY + 1.0),
                )
            }

            val newRange: Double = random.nextDouble(0.25, 1.5)

            val newResearchEquipmentPerTime: Double = if (targetResearchEquipmentPerTime > 0.0) {
                targetResearchEquipmentPerTime * random.nextDouble(0.5, 2.0)
            } else {
                0.0
            }

            val newMaxNumEmployee: Double = if(targetMaxEmployee > 0.0) {
                targetMaxEmployee * random.nextDouble(0.5, 2.0)
            } else {
                0.0
            }

            planDataAtPlayer.addCommand(
                BuildLaboratoryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    carrierId = carrierId,
                    laboratoryInternalData = LaboratoryInternalData(
                        xCor = newCor.x,
                        yCor = newCor.y,
                        range = newRange,
                        researchEquipmentPerTime = newResearchEquipmentPerTime,
                        maxNumEmployee = newMaxNumEmployee,
                        size = 0.0,
                    ),
                )
            )
        }
    }
}

/**
 * Consider building new laboratories at all carrier
 */
class RemoveLaboratoryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.map { (carrierId, carrier) ->
            carrier.allPopData.engineerPopData.laboratoryMap.map { (laboratoryId, _) ->
                RemoveSpecificLaboratoryReasoner(
                    carrierId = carrierId,
                    laboratoryId = laboratoryId,
                    random = random
                )
            }
        }.flatten()
}

/**
 * Consider building new laboratories at all carrier
 */
class RemoveSpecificLaboratoryReasoner(
    private val carrierId: Int,
    private val laboratoryId: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveSpecificLaboratoryOption(
            carrierId = carrierId,
            laboratoryId = laboratoryId,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        ),
    )
}

/**
 * Remove a specific laboratory
 */
class RemoveSpecificLaboratoryOption(
    private val carrierId: Int,
    private val laboratoryId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            OnlyOneLaboratoryConsideration(
                carrierId = carrierId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0
            ),
            SufficientLaboratoryConsideration(
                carrierId = carrierId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            KnownAppliedProjectInRangeConsideration(
                carrierId = carrierId,
                laboratoryId = laboratoryId,
                rankIfTrue = 1,
                multiplierIfTrue = 0.5,
                bonusIfTrue = 0.0,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveLaboratoryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                carrierId = carrierId,
                laboratoryId = laboratoryId,
            )
        )
    }
}