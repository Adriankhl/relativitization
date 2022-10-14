package relativitization.universe.mechanisms.defaults.dilated.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.PlayerScienceData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object KnowledgeDiffusion : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // The half edge length + 0.5 of the cube where diffusion happen
        val diffusionRange = 2

        // Diffusion probability
        val basicResearchDiffusionProb = 0.01
        val appliedResearchDiffusionProb = 0.001

        // Same top leader benefit
        val sameTopLeaderMultiplier = 5.0

        // Diffuse done projects by probability
        universeData3DAtPlayer.getNeighbourInCube(diffusionRange).forEach { playerData ->
            val actualBasicResearchDiffusionProb: Double =
                if (playerData.topLeaderId() == mutablePlayerData.topLeaderId()) {
                    basicResearchDiffusionProb * sameTopLeaderMultiplier
                } else {
                    basicResearchDiffusionProb
                }

            computeBasicResearchDiffusion(
                diffusionProb = actualBasicResearchDiffusionProb,
                thisScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                otherScienceData = playerData.playerInternalData.playerScienceData(),
                random = random,
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneBasicResearchProject(
                    it,
                    UpdateUniverseScienceData.basicResearchProjectFunction()
                )
            }

            val actualAppliedResearchDiffusionProb: Double =
                if (playerData.topLeaderId() == mutablePlayerData.topLeaderId()) {
                    appliedResearchDiffusionProb * sameTopLeaderMultiplier
                } else {
                    appliedResearchDiffusionProb
                }

            computeAppliedResearchDiffusion(
                diffusionProb = actualAppliedResearchDiffusionProb,
                thisScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                otherScienceData = playerData.playerInternalData.playerScienceData(),
                random = random,
            ).forEach {
                mutablePlayerData.playerInternalData.playerScienceData().doneAppliedResearchProject(
                    it,
                    UpdateUniverseScienceData.appliedResearchProjectFunction()
                )
            }
        }

        // Diffuse all visible project as known project
        universeData3DAtPlayer.getNeighbourInCube(diffusionRange).forEach { playerData ->
            val thisPlayerScienceData: MutablePlayerScienceData =
                mutablePlayerData.playerInternalData.playerScienceData()
            val otherPlayerScienceData: PlayerScienceData = playerData.playerInternalData.playerScienceData()

            (otherPlayerScienceData.doneBasicResearchProjectList + otherPlayerScienceData.knownBasicResearchProjectList)
                .filter {
                    !thisPlayerScienceData.isBasicProjectDone(it) && !thisPlayerScienceData.isBasicProjectKnown(it)
                }.forEach {
                    thisPlayerScienceData.knownBasicResearchProject(it)
                }


            (otherPlayerScienceData.doneAppliedResearchProjectList + otherPlayerScienceData.knownAppliedResearchProjectList)
                .filter {
                    !thisPlayerScienceData.isAppliedProjectDone(it) && !thisPlayerScienceData.isAppliedProjectKnown(it)
                }.forEach {
                    thisPlayerScienceData.knownAppliedResearchProject(it)
                }
        }

        return listOf()
    }

    fun computeBasicResearchDiffusion(
        diffusionProb: Double,
        thisScienceData: MutablePlayerScienceData,
        otherScienceData: PlayerScienceData,
        random: Random,
    ): List<BasicResearchProjectData> {
        return otherScienceData.doneBasicResearchProjectList.filter { otherProject ->
            !thisScienceData.isBasicProjectDone(otherProject)
        }.filter {
            random.nextDouble() < diffusionProb
        }.distinctBy { it.basicResearchId }
    }

    fun computeAppliedResearchDiffusion(
        diffusionProb: Double,
        thisScienceData: MutablePlayerScienceData,
        otherScienceData: PlayerScienceData,
        random: Random,
    ): List<AppliedResearchProjectData> {
        return otherScienceData.doneAppliedResearchProjectList.filter { otherProject ->
            !thisScienceData.isAppliedProjectDone(otherProject)
        }.filter {
            random.nextDouble() < diffusionProb
        }.distinctBy { it.appliedResearchId }
    }
}