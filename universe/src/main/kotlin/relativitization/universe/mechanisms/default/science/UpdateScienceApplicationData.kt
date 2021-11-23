package relativitization.universe.mechanisms.default.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.default.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.default.science.knowledge.MutableKnowledgeData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateScienceApplicationData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val scienceData: MutablePlayerScienceData =
            mutablePlayerData.playerInternalData.playerScienceData()

        scienceData.playerScienceApplicationData.idealShip =
            computeIdealShip(scienceData.playerKnowledgeData)

        return listOf()
    }

    private fun computeIdealShip(mutableKnowledgeData: MutableKnowledgeData): MutableCarrierInternalData {
        return MutableCarrierInternalData()
    }
}