package relativitization.universe.mechanisms.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object KnowledgeDiffusion : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // The size of the cube where diffusion happen
        val diffusionRange: Int = 1

        val basicResearchDiffusionProb: Double = 0.1
        val appliedResearchDiffusionProb: Double = 0.01

        return listOf()
    }
}