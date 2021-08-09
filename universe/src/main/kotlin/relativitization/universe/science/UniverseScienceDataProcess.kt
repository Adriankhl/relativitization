package relativitization.universe.science

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.science.knowledge.MutableBasicResearchData

abstract class UniverseScienceDataProcess {
    /**
     * Obtain a function encoding the effect of basic research project
     *
     * @param universeSettings the settings, for universeScienceDataProcessName
     * @return a function transforming MutableBasicResearchData
     */
    abstract fun basicResearchProjectFunction(
        universeSettings: UniverseSettings,
    ): (BasicResearchProjectData, MutableBasicResearchData) -> Unit


    /**
     * Obtain a function encoding the effect of applied research project
     *
     * @param universeSettings the settings, for universeScienceDataProcessName
     * @return a function transforming MutableBasicResearchData
     */
    abstract fun appliedResearchProjectFunction(
        universeSettings: UniverseSettings,
    ): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit

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

