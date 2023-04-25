package relativitization.universe.game.ai.defaults.node.self.construction

import relativitization.universe.game.ai.defaults.consideration.building.KnownBasicProjectInRangeConsideration
import relativitization.universe.game.ai.defaults.consideration.building.NoInstituteAtCarrierConsideration
import relativitization.universe.game.ai.defaults.consideration.building.OnlyOneInstituteConsideration
import relativitization.universe.game.ai.defaults.consideration.building.SufficientInstituteConsideration
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.commands.BuildInstituteCommand
import relativitization.universe.game.data.commands.RemoveInstituteCommand
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.InstituteInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.maths.physics.Double2D
import relativitization.universe.game.maths.physics.Intervals
import kotlin.random.Random

class InstituteReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            RemoveInstituteReasoner(random),
            NewInstituteReasoner(random),
        )
    }
}

/**
 * Consider building new institutes at all carrier
 */
class NewInstituteReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewInstituteAtCarrierReasoner(it, random)
        }
}

/**
 * Consider building new institute at a carrier
 */
class NewInstituteAtCarrierReasoner(
    private val carrierId: Int,
    private val random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            NewInstituteAtCarrierOption(
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
 * Option to build new institute
 */
class NewInstituteAtCarrierOption(
    private val carrierId: Int,
    private val random: Random,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            NoInstituteAtCarrierConsideration(
                carrierId = carrierId,
                rankIfTrue = 5,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0
            ),
            SufficientInstituteConsideration(
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
        // Determine number of new institute randomly
        val numNewInstitute: Int = random.nextInt(1, 6)

        // Number of carrier in Double
        val numCarrier: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().numCarrier()

        val knownBasicProjectList: List<BasicResearchProjectData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .knownBasicResearchProjectList

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

        // Determine the amount of research equipment per new institute
        val targetResearchEquipmentPerTime: Double = (totalResearchEquipment -
                researchEquipmentNeedPerTime) * 0.1 / numCarrier / numNewInstitute

        // Determine the employee per new institute
        val targetMaxEmployee: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .scholarPopData.commonPopData.adultPopulation * 0.1 / numNewInstitute

        (1..numNewInstitute).forEach { _ ->
            val instituteList: List<MutableInstituteData> = planDataAtPlayer
                .getCurrentMutablePlayerData().playerInternalData.popSystemData().carrierDataMap
                .getValue(carrierId).allPopData.scholarPopData.instituteMap.values.toList()

            // Filter known project which is not in range of any institute
            val outOfRangeBasicProject: List<BasicResearchProjectData> =
                knownBasicProjectList.filter { basicProject ->
                    instituteList.all { institute ->
                        Intervals.distance(
                            institute.instituteInternalData.xCor,
                            institute.instituteInternalData.yCor,
                            basicProject.xCor,
                            basicProject.yCor
                        ) <= institute.instituteInternalData.range
                    }
                }

            val newCor: Double2D = if (outOfRangeBasicProject.isNotEmpty()) {
                // Center at out-of-range project is it is not empty
                val targetProject: BasicResearchProjectData = outOfRangeBasicProject.first()
                Double2D(targetProject.xCor, targetProject.yCor)
            } else {
                val minInstituteX: Double = instituteList.minOfOrNull {
                    it.instituteInternalData.xCor - it.instituteInternalData.range
                } ?: 0.0
                val minInstituteY: Double = instituteList.minOfOrNull {
                    it.instituteInternalData.yCor - it.instituteInternalData.range
                } ?: 0.0
                val maxInstituteX: Double = instituteList.maxOfOrNull {
                    it.instituteInternalData.xCor + it.instituteInternalData.range
                } ?: 0.0
                val maxInstituteY: Double = instituteList.maxOfOrNull {
                    it.instituteInternalData.yCor + it.instituteInternalData.range
                } ?: 0.0

                // Randomly determine the knowledge coordinate of the new institute
                // Expand slightly from the existing institutes
                Double2D(
                    random.nextDouble(minInstituteX - 1.0, maxInstituteX + 1.0),
                    random.nextDouble(minInstituteY - 1.0, maxInstituteY + 1.0),
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
                BuildInstituteCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    carrierId = carrierId,
                    instituteInternalData = InstituteInternalData(
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
 * Consider building new institutes at all carrier
 */
class RemoveInstituteReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.map { (carrierId, carrier) ->
            carrier.allPopData.scholarPopData.instituteMap.map { (instituteId, _) ->
                RemoveSpecificInstituteReasoner(
                    carrierId = carrierId,
                    instituteId = instituteId,
                    random = random
                )
            }
        }.flatten()
}

/**
 * Consider building new institutes at all carrier
 */
class RemoveSpecificInstituteReasoner(
    private val carrierId: Int,
    private val instituteId: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        RemoveSpecificInstituteOption(
            carrierId = carrierId,
            instituteId = instituteId,
        ),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        ),
    )
}

/**
 * Remove a specific institute
 */
class RemoveSpecificInstituteOption(
    private val carrierId: Int,
    private val instituteId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            OnlyOneInstituteConsideration(
                carrierId = carrierId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0
            ),
            SufficientInstituteConsideration(
                carrierId = carrierId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            KnownBasicProjectInRangeConsideration(
                carrierId = carrierId,
                instituteId = instituteId,
                rankIfTrue = 1,
                multiplierIfTrue = 0.5,
                bonusIfTrue = 0.0,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveInstituteCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                carrierId = carrierId,
                instituteId = instituteId,
            )
        )
    }
}