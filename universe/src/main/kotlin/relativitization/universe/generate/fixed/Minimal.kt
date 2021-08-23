package relativitization.universe.generate.fixed

import relativitization.universe.data.*
import relativitization.universe.data.subsystem.physics.MutableInt4D
import relativitization.universe.data.subsystem.science.UniverseScienceData
import relativitization.universe.data.subsystem.science.knowledge.AppliedResearchField
import relativitization.universe.data.subsystem.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.subsystem.science.knowledge.BasicResearchField
import relativitization.universe.data.subsystem.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.science.default.DefaultUniverseScienceDataProcess

class Minimal : GenerateUniverse() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = copy(settings.universeSettings)

        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 4,
        )

        val playerData1 = MutablePlayerData(1)
        val playerData2 = MutablePlayerData(2)
        val playerData3 = MutablePlayerData(3)
        val playerData4 = MutablePlayerData(4)

        playerData1.playerType = PlayerType.HUMAN
        playerData3.int4D = MutableInt4D(0, 0, 0, 1)

        // Add one stellar to players
        playerData1.playerInternalData.popSystemData.addRandomStellarSystem()
        playerData3.playerInternalData.popSystemData.addRandomStellarSystem()

        // Add spaceShip
        playerData1.playerInternalData.popSystemData.addSpaceShip(1.0, 100.0, 100.0)
        playerData2.playerInternalData.popSystemData.addSpaceShip(1.0, 100.0, 100.0)
        playerData3.playerInternalData.popSystemData.addSpaceShip(1.0, 100.0, 100.0)
        playerData4.playerInternalData.popSystemData.addSpaceShip(1.0, 100.0, 100.0)


        // player 1 is a leader of player 2
        playerData2.playerInternalData.changeDirectLeaderId(playerData1.playerId)
        playerData1.playerInternalData.addDirectSubordinateId(playerData2.playerId)

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Change AI to EmptyAI
        playerData1.playerInternalData.aiData.aiName = "EmptyAI"
        playerData2.playerInternalData.aiData.aiName = "EmptyAI"
        playerData3.playerInternalData.aiData.aiName = "EmptyAI"
        playerData4.playerInternalData.aiData.aiName = "EmptyAI"

        // Add mathematics and energy project to player
        // Should sync to universe project at turn 3 since this has not been added to universe
        // science data
        playerData1.playerInternalData.playerScienceData.doneBasicResearchProject(
            BasicResearchProjectData(
                basicResearchId = 0,
                basicResearchField = BasicResearchField.MATHEMATICS,
                xCor = -1.0,
                yCor = -1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(),
                referenceAppliedResearchIdList = listOf()
            ),
            DefaultUniverseScienceDataProcess.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData.doneAppliedResearchProject(
            AppliedResearchProjectData(
                appliedResearchId = 0,
                appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                xCor = 1.0,
                yCor = -1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(),
                referenceAppliedResearchIdList = listOf()
            ),
            DefaultUniverseScienceDataProcess.appliedResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData.doneBasicResearchProject(
            BasicResearchProjectData(
                basicResearchId = 1,
                basicResearchField = BasicResearchField.MATHEMATICS,
                xCor = -1.0,
                yCor = 1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(0),
                referenceAppliedResearchIdList = listOf(0)
            ),
            DefaultUniverseScienceDataProcess.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData.doneAppliedResearchProject(
            AppliedResearchProjectData(
                appliedResearchId = 1,
                appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                xCor = 1.0,
                yCor = 1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(0, 1),
                referenceAppliedResearchIdList = listOf(0)
            ),
            DefaultUniverseScienceDataProcess.appliedResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData.knownBasicResearchProject(
            BasicResearchProjectData(
                basicResearchId = 2,
                basicResearchField = BasicResearchField.MATHEMATICS,
                xCor = -1.0,
                yCor = 2.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(0, 1),
                referenceAppliedResearchIdList = listOf(0, 1)
            )
        )
        playerData1.playerInternalData.playerScienceData.knownAppliedResearchProject(
            AppliedResearchProjectData(
                appliedResearchId = 2,
                appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                xCor = 1.0,
                yCor = 2.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(0, 1, 2),
                referenceAppliedResearchIdList = listOf(0, 1)
            )
        )


        data.addPlayerDataToLatestWithAfterImage(
            playerData1,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData2,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData3,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData4,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )

        return UniverseData(
            universeData4D = copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeScienceData = UniverseScienceData(),
        )
    }
}