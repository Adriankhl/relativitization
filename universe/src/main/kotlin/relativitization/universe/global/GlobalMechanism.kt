package relativitization.universe.global

import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.global.science.UniverseScienceDataProcess
import relativitization.universe.global.science.UniverseScienceDataProcessCollection
import relativitization.universe.utils.RelativitizationLogManager

object GlobalMechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    fun globalProcess(
        universeData: UniverseData
    ) {
        val mutableUniverseGlobalData: MutableUniverseGlobalData = DataSerializer.copy(universeData.universeGlobalData)

        UniverseScienceDataProcessCollection.processUniverseScienceData(mutableUniverseGlobalData, universeData)

        universeData.universeGlobalData = DataSerializer.copy(mutableUniverseGlobalData)
    }
}