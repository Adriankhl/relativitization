package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.components.default.science.knowledge.*
import relativitization.universe.data.components.default.science.application.MutableScienceApplicationData
import relativitization.universe.data.components.default.science.application.ScienceApplicationData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

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
 * @property playerScienceApplicationData the science product data of the player, based on playerKnowledgeData
 */
@Serializable
@SerialName("PlayerScienceData")
data class PlayerScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val doneBasicResearchProjectList: List<BasicResearchProjectData> = listOf(),
    val doneAppliedResearchProjectList: List<AppliedResearchProjectData> = listOf(),
    val knownBasicResearchProjectList: List<BasicResearchProjectData> = listOf(),
    val knownAppliedResearchProjectList: List<AppliedResearchProjectData> = listOf(),
    val playerKnowledgeData: KnowledgeData = KnowledgeData(),
    val playerScienceApplicationData: ScienceApplicationData = ScienceApplicationData(),
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("PlayerScienceData")
data class MutablePlayerScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    val doneBasicResearchProjectList: MutableList<BasicResearchProjectData> = mutableListOf(),
    val doneAppliedResearchProjectList: MutableList<AppliedResearchProjectData> = mutableListOf(),
    val knownBasicResearchProjectList: MutableList<BasicResearchProjectData> = mutableListOf(),
    val knownAppliedResearchProjectList: MutableList<AppliedResearchProjectData> = mutableListOf(),
    var playerKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var playerScienceApplicationData: MutableScienceApplicationData = MutableScienceApplicationData(),
) : MutableDefaultPlayerDataComponent() {
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
        if (doneBasicResearchProjectList.any {
                it.basicResearchId == basicResearchProjectData.basicResearchId
        }) {
            logger.error("Duplicate done basic research id: ${basicResearchProjectData.basicResearchId}, skipping")
        } else {
            knownBasicResearchProjectList.removeAll {
                logger.debug("Remove done project from known project")
                it.basicResearchId == basicResearchProjectData.basicResearchId
            }
            doneBasicResearchProjectList.add(basicResearchProjectData)
            playerKnowledgeData.addBasicResearchProjectData(basicResearchProjectData, function)
        }
    }

    /**
     * Done applied research project
     */
    fun doneAppliedResearchProject(
        appliedResearchProjectData: AppliedResearchProjectData,
        function: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit
    ) {
        if (doneAppliedResearchProjectList.any {
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }) {
            logger.error("Duplicate done applied research id: ${appliedResearchProjectData.appliedResearchId}, skipping")
        } else {
            knownAppliedResearchProjectList.removeAll {
                logger.debug("Remove done project from known project")
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }
            doneAppliedResearchProjectList.add(appliedResearchProjectData)
            playerKnowledgeData.addAppliedResearchProjectData(appliedResearchProjectData, function)
        }
    }

    /**
     * Known basic research project
     */
    fun knownBasicResearchProject(
        basicResearchProjectData: BasicResearchProjectData,
    ) {
        if ((doneBasicResearchProjectList + knownBasicResearchProjectList).any {
                it.basicResearchId == basicResearchProjectData.basicResearchId
            }) {
            logger.error("Duplicate known basic research id: ${basicResearchProjectData.basicResearchId}, skipping")
        } else {
            knownBasicResearchProjectList.add(basicResearchProjectData)
        }
    }

    /**
     * Known applied research project
     */
    fun knownAppliedResearchProject(
        appliedResearchProjectData: AppliedResearchProjectData,
    ) {
        if ((doneAppliedResearchProjectList + knownAppliedResearchProjectList).any {
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }) {
            logger.error("Duplicate known applied research id: ${appliedResearchProjectData.appliedResearchId}, skipping")
        } else {
            knownAppliedResearchProjectList.add(appliedResearchProjectData)
        }
    }

    /**
     * update common sense data
     *
     * @param newCommonSenseKnowledgeData new common sense
     * @param basicProjectFunction function of the effect of basic research projects
     * @param appliedProjectFunction function of the effect of applied research projects
     */
    fun updateCommonSenseData(
        newCommonSenseKnowledgeData: KnowledgeData,
        basicProjectFunction: (BasicResearchProjectData, MutableBasicResearchData) -> Unit,
        appliedProjectFunction: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit,
    ) {
        // Clear done projects
        doneBasicResearchProjectList.removeAll {
            it.basicResearchId < newCommonSenseKnowledgeData.startFromBasicResearchId
        }
        doneAppliedResearchProjectList.removeAll {
            it.appliedResearchId < newCommonSenseKnowledgeData.startFromAppliedResearchId
        }

        // Clear known projects
        knownBasicResearchProjectList.removeAll {
            it.basicResearchId < newCommonSenseKnowledgeData.startFromBasicResearchId
        }
        knownAppliedResearchProjectList.removeAll {
            it.appliedResearchId < newCommonSenseKnowledgeData.startFromAppliedResearchId
        }

        commonSenseKnowledgeData = DataSerializer.copy(newCommonSenseKnowledgeData)

        computePlayerKnowledgeData(basicProjectFunction, appliedProjectFunction)
    }

    /**
     * Sync project data from universe science data
     */
    fun syncProjectData(
        universeScienceData: UniverseScienceData,
        basicProjectFunction: (BasicResearchProjectData, MutableBasicResearchData) -> Unit,
        appliedProjectFunction: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit,
    ) {
        val syncDoneBasicResearchProjectList = doneBasicResearchProjectList.map {
            universeScienceData.basicResearchProjectDataMap.getOrElse(it.basicResearchId) {
                logger.error("Cannot find basic project ${it.basicResearchId} in universe data")
                it
            }
        }
        doneBasicResearchProjectList.clear()
        doneBasicResearchProjectList.addAll(syncDoneBasicResearchProjectList)

        val syncDoneAppliedResearchProjectList = doneAppliedResearchProjectList.map {
            universeScienceData.appliedResearchProjectDataMap.getOrElse(it.appliedResearchId) {
                logger.error("Cannot find applied project ${it.appliedResearchId} in universe data")
                it
            }
        }
        doneAppliedResearchProjectList.clear()
        doneAppliedResearchProjectList.addAll(syncDoneAppliedResearchProjectList)

        val syncKnownBasicResearchProjectList = knownBasicResearchProjectList.map {
            universeScienceData.basicResearchProjectDataMap.getOrElse(it.basicResearchId) {
                logger.error("Cannot find basic project ${it.basicResearchId} in universe data")
                it
            }
        }.filter { known ->
            !doneBasicResearchProjectList.any { known.basicResearchId == it.basicResearchId }
        }
        knownBasicResearchProjectList.clear()
        knownBasicResearchProjectList.addAll(syncKnownBasicResearchProjectList)

        val syncKnownAppliedResearchProjectList = knownAppliedResearchProjectList.map {
            universeScienceData.appliedResearchProjectDataMap.getOrElse(it.appliedResearchId) {
                logger.error("Cannot find applied project ${it.appliedResearchId} in universe data")
                it
            }
        }.filter { known ->
            !doneAppliedResearchProjectList.any { known.appliedResearchId == it.appliedResearchId }
        }
        knownAppliedResearchProjectList.clear()
        knownAppliedResearchProjectList.addAll(syncKnownAppliedResearchProjectList)

        computePlayerKnowledgeData(basicProjectFunction, appliedProjectFunction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}