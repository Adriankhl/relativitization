package relativitization.universe.science

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.science.UniverseScienceData
import relativitization.universe.data.component.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.component.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.component.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.component.science.knowledge.MutableBasicResearchData
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
}
