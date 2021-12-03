package relativitization.universe.mechanisms.defaults.dilated.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.PlayerScienceData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object KnowledgeDiffusion : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // The size of the cube where diffusion happen
        val diffusionRange: Int = 1

        // Diffusion probability
        val basicResearchDiffusionProb: Double = 0.01
        val appliedResearchDiffusionProb: Double = 0.001

        universeData3DAtPlayer.getNeighbour(diffusionRange).forEach { playerData ->
            computeBasicResearchDiffusion(
                basicResearchDiffusionProb,
                mutablePlayerData.playerInternalData.playerScienceData(),
                playerData.playerInternalData.playerScienceData(),
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneBasicResearchProject(
                    it,
                    UpdateUniverseScienceData.basicResearchProjectFunction()
                )
            }

            computeAppliedResearchDiffusion(
                appliedResearchDiffusionProb,
                mutablePlayerData.playerInternalData.playerScienceData(),
                playerData.playerInternalData.playerScienceData(),
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneAppliedResearchProject(
                    it,
                    UpdateUniverseScienceData.appliedResearchProjectFunction()
                )
            }
        }

        return listOf()
    }

    fun computeBasicResearchDiffusion(
        diffusionProb: Double,
        thisScienceData: MutablePlayerScienceData,
        otherScienceData: PlayerScienceData,
    ): List<BasicResearchProjectData> {
        return otherScienceData.doneBasicResearchProjectList.filter { otherProject ->
            !thisScienceData.doneBasicResearchProjectList.any { thisProject ->
                thisProject.basicResearchId == otherProject.basicResearchId
            }
        }.filter {
            Random.nextDouble() < diffusionProb
        }
    }

    fun computeAppliedResearchDiffusion(
        diffusionProb: Double,
        thisScienceData: MutablePlayerScienceData,
        otherScienceData: PlayerScienceData,
    ): List<AppliedResearchProjectData> {
        return otherScienceData.doneAppliedResearchProjectList.filter { otherProject ->
            !thisScienceData.doneAppliedResearchProjectList.any { thisProject ->
                thisProject.appliedResearchId == otherProject.appliedResearchId
            }
        }.filter {
            Random.nextDouble() < diffusionProb
        }
    }
}