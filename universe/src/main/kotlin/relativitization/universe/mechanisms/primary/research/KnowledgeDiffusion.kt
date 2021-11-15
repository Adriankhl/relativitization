package relativitization.universe.mechanisms.primary.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.PlayerScienceData
import relativitization.universe.data.components.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.global.science.default.DefaultUniverseScienceDataProcess
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.pow
import kotlin.random.Random

object KnowledgeDiffusion : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )


        // The size of the cube where diffusion happen
        val diffusionRange: Int = 1

        val basicResearchDiffusionProb: Double = 0.01
        val appliedResearchDiffusionProb: Double = 0.001

        val actualBasicResearchDiffusionProb: Double =
            1.0 - (1.0 - basicResearchDiffusionProb).pow(1.0 / gamma)

        val actualAppliedResearchDiffusionProb: Double =
            1.0 - (1.0 - appliedResearchDiffusionProb).pow(1.0 / gamma)


        universeData3DAtPlayer.getNeighbour(diffusionRange).forEach { playerData ->
            computeBasicResearchDiffusion(
                actualBasicResearchDiffusionProb,
                mutablePlayerData.playerInternalData.playerScienceData(),
                playerData.playerInternalData.playerScienceData(),
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneBasicResearchProject(
                    it,
                    DefaultUniverseScienceDataProcess.basicResearchProjectFunction()
                )
            }

            computeAppliedResearchDiffusion(
                actualAppliedResearchDiffusionProb,
                mutablePlayerData.playerInternalData.playerScienceData(),
                playerData.playerInternalData.playerScienceData(),
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneAppliedResearchProject(
                    it,
                    DefaultUniverseScienceDataProcess.appliedResearchProjectFunction()
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