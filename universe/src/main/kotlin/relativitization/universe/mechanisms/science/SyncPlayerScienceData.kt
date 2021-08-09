package relativitization.universe.mechanisms.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.science.ProcessUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.mechanisms.Mechanism

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
        mutablePlayerData.playerInternalData.playerScienceData.updateCommonSenseData(
            universeScienceData.commonSenseKnowledgeData,
            ProcessUniverseScienceData.basicResearchProjectFunction(universeSettings),
            ProcessUniverseScienceData.appliedResearchProjectFunction(universeSettings)
        )

        // sync research project data
        mutablePlayerData.playerInternalData.playerScienceData.syncProjectData(
            universeScienceData,
            ProcessUniverseScienceData.basicResearchProjectFunction(universeSettings),
            ProcessUniverseScienceData.appliedResearchProjectFunction(universeSettings)
        )

        return listOf()
    }
}