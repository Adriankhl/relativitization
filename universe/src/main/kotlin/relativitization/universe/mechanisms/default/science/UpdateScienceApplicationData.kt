package relativitization.universe.mechanisms.default.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.default.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.default.science.knowledge.MutableKnowledgeData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.algebra.Logistic
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.tanh

object UpdateScienceApplicationData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val scienceData: MutablePlayerScienceData =
            mutablePlayerData.playerInternalData.playerScienceData()

        // Remove ideal entertainment factory, the resource should be produced by entertainer directly
        scienceData.playerScienceApplicationData.idealResourceFactoryMap.remove(ResourceType.ENTERTAINMENT)

        scienceData.playerScienceApplicationData.idealSpaceship =
            computeIdealShip(scienceData.playerKnowledgeData)

        return listOf()
    }

    fun computeIdealShip(mutableKnowledgeData: MutableKnowledgeData): MutableCarrierInternalData {

        val coreRestMass: Double = mutableKnowledgeData.appliedResearchData.architectureTechnologyLevel * 1E6

        val maxMovementDeltaFuelRestMass : Double = coreRestMass *
                tanh(mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 100.0)

        val idealPopulation: Double = coreRestMass

        return MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxMovementDeltaFuelRestMass,
            size = 100.0,
            idealPopulation = idealPopulation
        )
    }
}