package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoInstituteAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientInstituteConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildInstituteCommand
import relativitization.universe.data.components.defaults.physics.Double2D
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.InstituteInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.random.Rand

class InstituteReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(NewInstituteReasoner())
    }
}

/**
 * Consider building new institutes at all carrier
 */
class NewInstituteReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewInstituteAtCarrierReasoner(it)
        }
}

/**
 * Consider building a new institute at a carrier
 */
class NewInstituteAtCarrierReasoner(
    private val carrierId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            NewInstituteAtCarrierOption(carrierId),
            DoNothingDualUtilityOption(1, 1.0, 1.0)
        )
    }
}

class NewInstituteAtCarrierOption(
    private val carrierId: Int,
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
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Build multiple new institute randomly
        val numNewInstitute: Int = Rand.rand().nextInt(1, 6)

        val doneBasicProjectList: List<BasicResearchProjectData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .doneBasicResearchProjectList
        val knownBasicProjectList: List<BasicResearchProjectData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .knownBasicResearchProjectList

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
                    Rand.rand().nextDouble(minInstituteX - 1.0, maxInstituteX + 1.0),
                    Rand.rand().nextDouble(minInstituteY - 1.0, maxInstituteY + 1.0),
                )
            }

            val newRange: Double = Rand.rand().nextDouble(0.25, 1.5)

            planDataAtPlayer.addCommand(
                BuildInstituteCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    carrierId = carrierId,
                    instituteInternalData = InstituteInternalData(
                        xCor = newCor.x,
                        yCor = newCor.y,
                        range = newRange,
                        researchEquipmentPerTime = 0.0,
                        maxNumEmployee = 0.0,
                        size = 0.0,
                    ),
                )
            )
        }
    }
}