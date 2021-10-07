package relativitization.universe.generate.fixed

import relativitization.universe.ai.emptyAI.EmptyAI
import relativitization.universe.ai.name
import relativitization.universe.data.*
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.MutableInt4D
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.UniverseScienceData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryInternalData
import relativitization.universe.data.component.science.knowledge.AppliedResearchField
import relativitization.universe.data.component.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.component.science.knowledge.BasicResearchField
import relativitization.universe.data.component.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.science.default.DefaultUniverseScienceDataProcess

class TestingFixedMinimal : GenerateUniverse() {
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
        playerData1.playerInternalData.popSystemData().addRandomStellarSystem()
        playerData3.playerInternalData.popSystemData().addRandomStellarSystem()

        // Add spaceShip
        playerData1.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0)
        playerData2.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0)
        playerData3.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0)
        playerData4.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0)

        // Add fuel rest mass
        playerData1.playerInternalData.physicsData().fuelRestMassData.movement = 100.0
        playerData2.playerInternalData.physicsData().fuelRestMassData.movement = 100.0
        playerData3.playerInternalData.physicsData().fuelRestMassData.movement = 100.0
        playerData4.playerInternalData.physicsData().fuelRestMassData.movement = 100.0


        // player 1 is a leader of player 2
        playerData2.playerInternalData.changeDirectLeaderId(
            playerData1.playerId,
            playerData1.playerInternalData.leaderIdList
        )
        playerData1.playerInternalData.addDirectSubordinateId(playerData2.playerId)

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Change AI to EmptyAI
        playerData1.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData2.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData3.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData4.playerInternalData.aiData().aiName = EmptyAI.name()

        // Add mathematics and energy project to player
        // Should sync to universe project at turn 3 since this has not been added to universe
        // science data
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
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
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
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
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
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
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
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
        playerData1.playerInternalData.playerScienceData().knownBasicResearchProject(
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
        playerData1.playerInternalData.playerScienceData().knownAppliedResearchProject(
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

        // Add ideal factory to player 1
        playerData1.playerInternalData.playerScienceData().playerScienceProductData.idealFactoryMap[ResourceType.FUEL] =
                MutableFactoryInternalData(
                    outputResource = ResourceType.FUEL,
                    maxOutputResourceQualityData = MutableResourceQualityData(),
                    maxOutputAmount = 1.0,
                    inputResourceMap = mutableMapOf(),
                    fuelRestMassConsumptionRate = 0.0,
                    maxNumEmployee = 10.0,
                    size = 1.0,
                )

        // Add population to player 1
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.commonPopData.adultPopulation = 100.0

        // Add fuel factory to player 1
        val fuelFactory1 = MutableFactoryData(
            ownerPlayerId = 1,
            factoryInternalData = MutableFactoryInternalData(
                outputResource = ResourceType.FUEL,
                maxOutputResourceQualityData = MutableResourceQualityData(
                    quality1 = 0.0,
                    quality2 = 0.0,
                    quality3 = 0.0,
                ),
                maxOutputAmount = 2.0,
                inputResourceMap = mutableMapOf(),
                fuelRestMassConsumptionRate = 0.0,
                maxNumEmployee = 100.0,
                size = 3.0,
            ),
            numBuilding = 1,
            isOpened = true,
            lastOutputAmount = 0.0,
            lastNumEmployee = 100.0,
            lastInputAmountMap = mutableMapOf(),
            storedFuelRestMass = 0.0,
        )
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.factoryMap[1] = fuelFactory1


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