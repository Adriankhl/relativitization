package relativitization.universe.global.science

import relativitization.universe.data.*
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.components.default.science.knowledge.MutableBasicResearchData
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.components.science.MutableUniverseScienceData
import relativitization.universe.data.global.components.science.UniverseScienceData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.global.science.default.DefaultUniverseScienceDataProcess
import relativitization.universe.global.science.empty.EmptyUniverseScienceDataProcess
import relativitization.universe.utils.RelativitizationLogManager

abstract class UniverseScienceDataProcess {
    /**
     * Obtain a function encoding the effect of basic research project
     *
     * @return a function transforming MutableBasicResearchData
     */
    abstract fun basicResearchProjectFunction(): (BasicResearchProjectData, MutableBasicResearchData) -> Unit


    /**
     * Obtain a function encoding the effect of applied research project
     *
     * @return a function transforming MutableBasicResearchData
     */
    abstract fun appliedResearchProjectFunction(): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit

    /**
     * Generate new universe science data per turn
     * Should generate new projects and new common sense
     *
     * @param universeScienceData the universe science data to be processed
     * @param universeSettings the settings of the universe
     * @return the new universe science data
     */
    abstract fun newUniverseScienceData(
        universeScienceData: UniverseScienceData,
        universeSettings: UniverseSettings,
    ): UniverseScienceData

}

fun UniverseScienceDataProcess.name(): String = this::class.simpleName.toString()

object UniverseScienceDataProcessCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val universeScienceDataProcessList: List<UniverseScienceDataProcess> = listOf(
        DefaultUniverseScienceDataProcess,
        EmptyUniverseScienceDataProcess,
    )

    val universeScienceDataProcessNameMap: Map<String, UniverseScienceDataProcess> =
        universeScienceDataProcessList.map {
            it.name() to it
        }.toMap()

    fun getProcess(universeSettings: UniverseSettings): UniverseScienceDataProcess {
        return universeScienceDataProcessNameMap.getOrElse(
            universeSettings.universeScienceDataProcessCollectionName
        ) {
            logger.error("No universe science process name: ${universeSettings.universeScienceDataProcessCollectionName}," +
                    " using default universe science data process")
            DefaultUniverseScienceDataProcess
        }
    }

    fun processUniverseScienceData(
        mutableUniverseGlobalData: MutableUniverseGlobalData,
        universeData: UniverseData,
    ) {

        // Update universe common sense
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(mutableUniverseGlobalData.getScienceData())

        val allVisiblePlayerData: List<PlayerData> = universeData.getAllVisiblePlayerData()

        val newStartFromBasicResearchId: Int = allVisiblePlayerData.minOfOrNull {
            it.playerInternalData.playerScienceData().playerKnowledgeData.startFromBasicResearchId
        } ?: 0

        val newStartFromAppliedResearchId: Int = allVisiblePlayerData.minOfOrNull {
            it.playerInternalData.playerScienceData().playerKnowledgeData.startFromAppliedResearchId
        } ?: 0

        mutableUniverseScienceData.updateCommonSenseData(
            newStartFromBasicResearchId = newStartFromBasicResearchId,
            newStartFromAppliedResearchId = newStartFromAppliedResearchId,
            basicProjectFunction = getProcess(
                universeData.universeSettings
            ).basicResearchProjectFunction(),
            appliedProjectFunction = getProcess(
                universeData.universeSettings
            ).appliedResearchProjectFunction(),
        )

        // Generate new projects
        val newUniverseScienceData: UniverseScienceData = getProcess(
            universeData.universeSettings
        ).newUniverseScienceData(
            DataSerializer.copy(mutableUniverseScienceData),
            universeData.universeSettings
        )

        // Modify the universe science data
        mutableUniverseGlobalData.updateScienceData(newUniverseScienceData)
    }
}