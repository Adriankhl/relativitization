package relativitization.universe.global.science.empty

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseScienceData
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.MutableAppliedResearchData
import relativitization.universe.data.components.default.science.knowledge.MutableBasicResearchData
import relativitization.universe.global.science.UniverseScienceDataProcess

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