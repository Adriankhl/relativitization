package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.data.science.product.MutableScienceProductData
import relativitization.universe.data.science.product.ScienceProductData
import relativitization.universe.data.serializer.DataSerializer

/**
 * Science data of a player
 *
 * @property commonSenseKnowledgeData the common sense knowledge, should be the same for all players
 * @property doneBasicResearchProjectList the completed basic research projects for this player
 * @property doneAppliedResearchProjectList the completed applied research projects for this player
 * @property knownBasicResearchProjectList this player know these basic research projects exist
 * @property knownAppliedResearchProjectList this player know these applied research projects exist
 * @property playerKnowledgeData the knowledge data own by the player, based on common sense and
 *  done projects
 * @property playerScienceProductData the science product data of the player, based on playerKnowledgeData
 */
@Serializable
data class PlayerScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val doneBasicResearchProjectList: List<BasicResearchProjectData> = listOf(),
    val doneAppliedResearchProjectList: List<AppliedResearchProjectData> = listOf(),
    val knownBasicResearchProjectList: List<BasicResearchProjectData> = listOf(),
    val knownAppliedResearchProjectList: List<AppliedResearchProjectData> = listOf(),
    val playerKnowledgeData: KnowledgeData = KnowledgeData(),
    val playerScienceProductData: ScienceProductData = ScienceProductData(),
)

@Serializable
data class MutablePlayerScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    val doneBasicResearchProjectList: MutableList<BasicResearchProjectData> = mutableListOf(),
    val doneAppliedResearchProjectList: MutableList<AppliedResearchProjectData> = mutableListOf(),
    val knownBasicResearchProjectList: MutableList<BasicResearchProjectData> = mutableListOf(),
    val knownAppliedResearchProjectList: MutableList<AppliedResearchProjectData> = mutableListOf(),
    var playerKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var playerScienceProductData: MutableScienceProductData = MutableScienceProductData(),
) {
    /**
     * Compute player knowledge data by common sense and knowledge data list
     */
    fun computePlayerKnowledgeData(
        basicProjectFunction: (BasicResearchProjectData, MutableBasicResearchData) -> Unit,
        appliedProjectFunction: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit,
    ) {
        playerKnowledgeData = DataSerializer.copy(commonSenseKnowledgeData)
        doneBasicResearchProjectList.forEach {
            playerKnowledgeData.addBasicResearchProjectData(
                it,
                basicProjectFunction
            )
        }

        doneAppliedResearchProjectList.forEach {
            playerKnowledgeData.addAppliedResearchProjectData(
                it,
                appliedProjectFunction
            )
        }
    }

    /**
     * Done basic research project
     */
    fun doneBasicResearchProject(
        basicResearchProjectData: BasicResearchProjectData,
        function: (BasicResearchProjectData, MutableBasicResearchData) -> Unit
    ) {
        doneBasicResearchProjectList.add(basicResearchProjectData)
        playerKnowledgeData.addBasicResearchProjectData(basicResearchProjectData, function)
    }

    /**
     * Done applied research project
     */
    fun doneAppliedResearchProject(
        appliedResearchProjectData: AppliedResearchProjectData,
        function: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit
    ) {
        doneAppliedResearchProjectList.add(appliedResearchProjectData)
        playerKnowledgeData.addAppliedResearchProjectData(appliedResearchProjectData, function)
    }
}