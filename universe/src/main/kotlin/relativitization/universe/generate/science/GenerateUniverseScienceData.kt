package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

object GenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun generate(universeData: UniverseData): UniverseScienceData {
        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(universeData)
        return DataSerializer.copy(mutableUniverseScienceData)
    }
}