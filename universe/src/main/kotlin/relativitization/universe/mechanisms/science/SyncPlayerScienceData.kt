package relativitization.universe.mechanisms.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.UniverseScienceData
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.global.science.UniverseScienceDataProcessCollection

/**
 * Sync player science data with universe science data
 */
object SyncPlayerScienceData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {

        // update player common sense
        mutablePlayerData.playerInternalData.playerScienceData().updateCommonSenseData(
            universeScienceData.commonSenseKnowledgeData,
            UniverseScienceDataProcessCollection.getProcess(
                universeSettings
            ).basicResearchProjectFunction(),
            UniverseScienceDataProcessCollection.getProcess(
                universeSettings
            ).appliedResearchProjectFunction()
        )

        // sync research project data
        mutablePlayerData.playerInternalData.playerScienceData().syncProjectData(
            universeScienceData,
            UniverseScienceDataProcessCollection.getProcess(
                universeSettings
            ).basicResearchProjectFunction(),
            UniverseScienceDataProcessCollection.getProcess(
                universeSettings
            ).appliedResearchProjectFunction()
        )

        return listOf()
    }
}