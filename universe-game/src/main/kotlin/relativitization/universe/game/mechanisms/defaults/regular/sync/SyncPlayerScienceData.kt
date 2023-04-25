package relativitization.universe.game.mechanisms.defaults.regular.sync

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.data.global.components.universeScienceData
import relativitization.universe.game.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.game.mechanisms.Mechanism
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