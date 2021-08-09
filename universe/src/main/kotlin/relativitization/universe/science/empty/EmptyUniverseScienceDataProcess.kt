package relativitization.universe.science.empty

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.science.knowledge.MutableBasicResearchData
import relativitization.universe.science.UniverseScienceDataProcess

object EmptyUniverseScienceDataProcess : UniverseScienceDataProcess() {
    override fun basicResearchProjectFunction(): (BasicResearchProjectData, MutableBasicResearchData) -> Unit {
        return { _, _ -> }
    }

    override fun appliedResearchProjectFunction(): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit {
        return { _, _ -> }
    }

    override fun newUniverseScienceData(
        universeScienceData: UniverseScienceData,
        universeSettings: UniverseSettings
    ): UniverseScienceData {
        return universeScienceData
    }
}