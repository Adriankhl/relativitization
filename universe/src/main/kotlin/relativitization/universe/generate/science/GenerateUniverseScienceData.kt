package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.utils.RelativitizationLogManager

object GenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun generate(universeData: UniverseData): UniverseScienceData {
        return universeData.universeScienceData
    }
}