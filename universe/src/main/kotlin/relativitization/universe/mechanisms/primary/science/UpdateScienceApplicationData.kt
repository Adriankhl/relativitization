package relativitization.universe.mechanisms.primary.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.science.knowledge.MutableKnowledgeData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.exp

object UpdateScienceApplicationData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val scienceData: MutablePlayerScienceData = mutablePlayerData.playerInternalData.playerScienceData()

        scienceData.playerScienceApplicationData.maxShipRestMass =
            maxRestShipMass(scienceData.playerKnowledgeData)

        scienceData.playerScienceApplicationData.maxShipEnginePowerByRestMass =
            maxShipEnginePowerByRestMass(scienceData.playerKnowledgeData)

        return listOf()
    }

    private fun maxRestShipMass(mutableKnowledgeData: MutableKnowledgeData): Double {
        return 1.0E6 + mutableKnowledgeData.appliedResearchData.architectureTechnologyLevel * 100.0
    }

    private fun maxShipEnginePowerByRestMass(mutableKnowledgeData: MutableKnowledgeData): Double {
        return 1.0 / (1.0 + exp(mutableKnowledgeData.appliedResearchData.energyTechnologyLevel))
    }

}