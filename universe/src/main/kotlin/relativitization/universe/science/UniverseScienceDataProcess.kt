package relativitization.universe.science

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.science.knowledge.MutableBasicResearchData
import relativitization.universe.science.default.DefaultUniverseScienceDataProcess
import relativitization.universe.science.empty.EmptyUniverseScienceDataProcess
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

object UniverseScienceDataProcessCollection {
    private val logger = RelativitizationLogManager.getLogger()

    val universeScienceDataProcessNameMap: Map<String, UniverseScienceDataProcess> = mapOf(
        "DefaultScience" to DefaultUniverseScienceDataProcess,
        "EmptyScience" to EmptyUniverseScienceDataProcess,
    )

    fun getProcess(universeSettings: UniverseSettings): UniverseScienceDataProcess {
        return universeScienceDataProcessNameMap.getOrElse(
            universeSettings.universeScienceDataProcessCollectionName
        ) {
            logger.error("No universe science process name: ${universeSettings.universeScienceDataProcessCollectionName}," +
                    " using default universe science data process")
            DefaultUniverseScienceDataProcess
        }
    }
}