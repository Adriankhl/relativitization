package relativitization.universe.generate.fixed

import relativitization.universe.data.*
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.science.DefaultProcessUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.AppliedResearchField
import relativitization.universe.data.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.science.knowledge.BasicResearchField
import relativitization.universe.data.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid

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
        playerData1.playerInternalData.popSystemicData.addRandomStellarSystem()
        playerData3.playerInternalData.popSystemicData.addRandomStellarSystem()

        // Add spaceShip
        playerData1.playerInternalData.popSystemicData.addSpaceShip(1.0, 100.0, 100.0)
        playerData2.playerInternalData.popSystemicData.addSpaceShip(1.0, 100.0, 100.0)
        playerData3.playerInternalData.popSystemicData.addSpaceShip(1.0, 100.0, 100.0)
        playerData4.playerInternalData.popSystemicData.addSpaceShip(1.0, 100.0, 100.0)


        // player 1 is a leader of player 2
        playerData2.playerInternalData.changeDirectLeaderId(playerData1.id)
        playerData1.playerInternalData.addDirectSubordinateId(playerData2.id)

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Change AI to EmptyAI
        playerData1.playerInternalData.aiData.aiName = "EmptyAI"
        playerData2.playerInternalData.aiData.aiName = "EmptyAI"
        playerData3.playerInternalData.aiData.aiName = "EmptyAI"
        playerData4.playerInternalData.aiData.aiName = "EmptyAI"

        // Add mathematics and energy project
        playerData1.playerInternalData.playerScienceData.doneBasicResearchProject(
            BasicResearchProjectData(
                basicResearchId = 0,
                basicResearchField = BasicResearchField.MATHEMATICS,
                xCor = 1.0,
                yCor = 1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(),
                referenceAppliedResearchIdList = listOf()
            ),
            DefaultProcessUniverseScienceData.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData.doneAppliedResearchProject(
            AppliedResearchProjectData(
                appliedResearchId = 0,
                appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                xCor = -1.0,
                yCor = -1.0,
                difficulty = 1.0,
                significance = 1.0,
                referenceBasicResearchIdList = listOf(),
                referenceAppliedResearchIdList = listOf()
            ),
            DefaultProcessUniverseScienceData.appliedResearchProjectFunction()
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