package relativitization.universe.generate.method.testing

import relativitization.universe.ai.EmptyAI
import relativitization.universe.ai.name
import relativitization.universe.data.*
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.MutableDouble4D
import relativitization.universe.data.components.defaults.physics.MutableInt4D
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.maths.grid.Grids.create4DGrid

object TestingFixedMinimal : TestingGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = copy(settings.universeSettings)

        val mutableUniverseData4D = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        // Global data first
        val mutableUniverseGlobalData = MutableUniverseGlobalData()

        // Create basic and applied project
        val basic0 = BasicResearchProjectData(
            basicResearchId = 0,
            basicResearchField = BasicResearchField.MATHEMATICS,
            xCor = -1.0,
            yCor = -1.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(),
            referenceAppliedResearchIdList = listOf()
        )
        val basic1 = BasicResearchProjectData(
            basicResearchId = 1,
            basicResearchField = BasicResearchField.MATHEMATICS,
            xCor = -1.0,
            yCor = 1.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(0),
            referenceAppliedResearchIdList = listOf(0)
        )
        val basic2 = BasicResearchProjectData(
            basicResearchId = 2,
            basicResearchField = BasicResearchField.MATHEMATICS,
            xCor = -1.0,
            yCor = 2.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(0, 1),
            referenceAppliedResearchIdList = listOf(0, 1)
        )
        val applied0 = AppliedResearchProjectData(
            appliedResearchId = 0,
            appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
            xCor = 1.0,
            yCor = -1.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(),
            referenceAppliedResearchIdList = listOf()
        )
        val applied1 = AppliedResearchProjectData(
            appliedResearchId = 1,
            appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
            xCor = 1.0,
            yCor = 1.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(0, 1),
            referenceAppliedResearchIdList = listOf(0)
        )
        val applied2 = AppliedResearchProjectData(
            appliedResearchId = 2,
            appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
            xCor = 1.0,
            yCor = 2.0,
            difficulty = 1.0,
            significance = 1.0,
            referenceBasicResearchIdList = listOf(0, 1, 2),
            referenceAppliedResearchIdList = listOf(0, 1)
        )

        // Add project to universe science data
        val mutableUniverseScienceData = mutableUniverseGlobalData.universeScienceData()
        mutableUniverseScienceData.addBasicResearchProjectData(basic0)
        mutableUniverseScienceData.addBasicResearchProjectData(basic1)
        mutableUniverseScienceData.addBasicResearchProjectData(basic2)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied0)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied1)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied2)


        // Totally 6 player
        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 6,
        )
        val playerData1 = MutablePlayerData(1)
        val playerData2 = MutablePlayerData(2)
        val playerData3 = MutablePlayerData(3)
        val playerData4 = MutablePlayerData(4)
        val playerData5 = MutablePlayerData(5)
        val playerData6 = MutablePlayerData(6)


        // Change AI to EmptyAI to for deterministic testing
        playerData1.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData2.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData3.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData4.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData5.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData6.playerInternalData.aiData().aiName = EmptyAI.name()


        // Player 1 data

        // Only player 1 is human
        playerData1.playerType = PlayerType.HUMAN

        // Move player 1 to (0.1, 0.1, 0.1) to avoid boundary
        playerData1.double4D = MutableDouble4D(0.0, 0.1, 0.1, 0.1)

        // Add carrier system to player 1
        playerData1.playerInternalData.popSystemData().addStellarSystem(1E30)
        playerData1.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0, 1000.0)

        // Add fuel and resource to player 1
        playerData1.playerInternalData.physicsData().fuelRestMassData.production = 30.0
        playerData1.playerInternalData.economyData().resourceData.addNewResource(
            ResourceType.PLANT,
            MutableResourceQualityData(
                1.0,
                2.0,
                3.0
            ),
            5.0,
        )

        // Player 2 is a subordinate of player 1
        playerData1.addDirectSubordinateId(playerData2.playerId)

        // Add mathematics and energy project to player 1
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
            basic0,
            UpdateUniverseScienceData.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
            applied0,
            UpdateUniverseScienceData.appliedResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
            basic1,
            UpdateUniverseScienceData.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
            applied1,
            UpdateUniverseScienceData.appliedResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().knownBasicResearchProject(
            basic2
        )
        playerData1.playerInternalData.playerScienceData().knownAppliedResearchProject(
            applied2
        )

        // Add a research institute with high research power to player 1
        val mutableInstitute: MutableInstituteData = MutableInstituteData(
            xCor = -1.0,
            yCor = 2.0,
            range = 0.25,
            strength = 1.0,
            reputation = 0.0,
            researchEquipmentPerTime = 0.0,
            maxNumEmployee = Double.MAX_VALUE * 0.5,
            lastNumEmployee = 0.0,
            size = 0.0
        )
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.scholarPopData.addInstitute(mutableInstitute)

        // Add a laboratory to player 1
        val mutableLaboratoryData: MutableLaboratoryData = MutableLaboratoryData(
            xCor = 1.0,
            yCor = 2.0,
            range = 0.25,
            strength = 0.0,
            reputation = 0.0,
            researchEquipmentPerTime = 0.0,
            maxNumEmployee = Double.MAX_VALUE * 0.5,
            lastNumEmployee = 0.0,
            size = 0.0
        )
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.engineerPopData.addLaboratory(mutableLaboratoryData)



        // Add ideal fuel factory to player 1
        playerData1.playerInternalData.playerScienceData().playerScienceApplicationData.idealFuelFactory =
            MutableFuelFactoryInternalData(
                maxOutputAmount = 1.0,
                maxNumEmployee = 10.0,
                size = 1.0,
            )

        // Add ideal plant factory to player 1
        playerData1.playerInternalData.playerScienceData().playerScienceApplicationData.idealResourceFactoryMap[ResourceType.PLANT] =
            MutableResourceFactoryInternalData(
                outputResource = ResourceType.PLANT,
                maxOutputResourceQualityData = MutableResourceQualityData(
                    quality1 = 0.0,
                    quality2 = 0.0,
                    quality3 = 0.0,
                ),
                maxOutputAmount = 1.0,
                inputResourceMap = mutableMapOf(),
                fuelRestMassConsumptionRate = 1.0,
                maxNumEmployee = 100.0,
                size = 1.0
            )

        // Add population to player 1
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.commonPopData.adultPopulation = 100.0
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.scholarPopData.commonPopData.adultPopulation = Double.MAX_VALUE
        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.engineerPopData.commonPopData.adultPopulation = Double.MAX_VALUE


        // Add fuel factory to player 1
        val fuelFactory1 = MutableFuelFactoryData(
            ownerPlayerId = 1,
            fuelFactoryInternalData = MutableFuelFactoryInternalData(
                maxOutputAmount = 2.0,
                maxNumEmployee = 100.0,
                size = 3.0,
            ),
            numBuilding = 1.0,
            isOpened = true,
            storedFuelRestMass = 0.0,
            lastOutputAmount = 0.0,
            lastNumEmployee = 0.0,
        )

        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.addFuelFactory(fuelFactory1)

        // Player 2

        // Move player 2 to (0.1, 0.1, 0.1) to avoid boundary
        playerData2.double4D = MutableDouble4D(0.0, 0.1, 0.1, 0.1)

        // Add spaceship to player 2
        playerData2.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0, 1000.0)

        // Add fuel to player 2 for movement
        playerData2.playerInternalData.physicsData().fuelRestMassData.movement = 100.0

        // player 1 is a leader of player 2
        playerData2.changeDirectLeaderId(
            playerData1.playerInternalData.leaderIdList
        )

        // Player 3

        // Move player 3 to (0, 0, 1)
        playerData3.int4D = MutableInt4D(0, 0, 0, 1)

        // Add carrier system to player 3
        playerData3.playerInternalData.popSystemData().addStellarSystem(2E30)
        playerData3.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0, 1000.0)

        // Add fuel to player 3
        playerData3.playerInternalData.physicsData().fuelRestMassData.movement = 100.0

        // Player 4

        // Add spaceShip to player 4
        playerData4.playerInternalData.popSystemData().addSpaceShip(1.0, 100.0, 1000.0)

        // Add fuel to player 4
        playerData4.playerInternalData.physicsData().fuelRestMassData.movement = 100.0

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Player 5

        // Move player 5 to (1, 0, 0)
        playerData5.int4D = MutableInt4D(0, 1, 0, 0)

        // Add stellar system to player 5
        playerData5.playerInternalData.popSystemData().addStellarSystem(1.5E30)

        // Add soldier and military base
        playerData5.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.soldierPopData.commonPopData.adultPopulation = 1000.0
        playerData5.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.soldierPopData.commonPopData.satisfaction = 100.0
        playerData5.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.soldierPopData.militaryBaseData.shield = 100.0

        // Player 6

        // Move player 6 to (1.4, 0, 0)
        playerData6.int4D = MutableInt4D(0, 1, 0, 0)
        playerData6.double4D = MutableDouble4D(0.0, 1.4, 0.0, 0.0)

        // Add stellar system to player 6
        playerData6.playerInternalData.popSystemData().addStellarSystem(1.5E30)

        // Add shield
        playerData5.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.soldierPopData.militaryBaseData.shield = 2000.0


        // Add player data to universe data 4D
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData1,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData2,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData3,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData4,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData5,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
            playerData6,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )

        return UniverseData(
            universeData4D = copy(mutableUniverseData4D),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = copy(mutableUniverseGlobalData),
        )
    }
}