package relativitization.universe.mechanisms.defaults.regular.sync

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.global.components.universeScienceData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

/**
 * Sync player science data with universe science data
 */
object SyncPlayerScienceData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // update player common sense
        mutablePlayerData.playerInternalData.playerScienceData().updateCommonSenseData(
            universeGlobalData.universeScienceData().commonSenseKnowledgeData,
            UpdateUniverseScienceData.basicResearchProjectFunction(),
            UpdateUniverseScienceData.appliedResearchProjectFunction(),
        )

        // sync research project data
        mutablePlayerData.playerInternalData.playerScienceData().syncProjectData(
            universeGlobalData.universeScienceData(),
            UpdateUniverseScienceData.basicResearchProjectFunction(),
            UpdateUniverseScienceData.appliedResearchProjectFunction(),
        )

        return listOf()
    }
}