package relativitization.universe.generate.method.testing

import relativitization.universe.ai.EmptyAI
import relativitization.universe.ai.name
import relativitization.universe.data.*
import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.default.physics.MutableInt4D
import relativitization.universe.data.components.default.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.components.default.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.BasicResearchField
import relativitization.universe.data.components.default.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethod
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.global.science.default.DefaultUniverseScienceDataProcess

object TestingFixedMinimal : GenerateUniverseMethod() {
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
        playerData1.playerInternalData.popSystemData().addStellarSystem(1E30)
        playerData3.playerInternalData.popSystemData().addStellarSystem(2E30)

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

        playerData1.playerInternalData.physicsData().fuelRestMassData.production = 30.0

        // Add resource to player 1
        playerData1.playerInternalData.economyData().resourceData.addNewResource(
            ResourceType.PLANT,
            MutableResourceQualityData(
                1.0,
                2.0,
                3.0
            ),
            5.0,
        )


        // player 1 is a leader of player 2
        playerData2.changeDirectLeaderId(
            playerData1.playerInternalData.leaderIdList
        )
        playerData1.addDirectSubordinateId(playerData2.playerId)

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Change AI to EmptyAI
        playerData1.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData2.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData3.playerInternalData.aiData().aiName = EmptyAI.name()
        playerData4.playerInternalData.aiData().aiName = EmptyAI.name()

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

        // Add mathematics and energy project to player
        // Also need to add to universe science data later
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
            basic0,
            DefaultUniverseScienceDataProcess.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
            applied0,
            DefaultUniverseScienceDataProcess.appliedResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneBasicResearchProject(
            basic1,
            DefaultUniverseScienceDataProcess.basicResearchProjectFunction()
        )
        playerData1.playerInternalData.playerScienceData().doneAppliedResearchProject(
            applied1,
            DefaultUniverseScienceDataProcess.appliedResearchProjectFunction()
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
            numBuilding = 1,
            isOpened = true,
            storedFuelRestMass = 0.0,
            lastOutputAmount = 0.0,
            lastNumEmployee = 0.0,
        )

        playerData1.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.addFuelFactory(fuelFactory1)


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

        // Add project to universe science data
        val mutableUniverseScienceData = MutableUniverseScienceData()
        mutableUniverseScienceData.addBasicResearchProjectData(basic0)
        mutableUniverseScienceData.addBasicResearchProjectData(basic1)
        mutableUniverseScienceData.addBasicResearchProjectData(basic2)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied0)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied1)
        mutableUniverseScienceData.addAppliedResearchProjectData(applied2)

        return UniverseData(
            universeData4D = copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = UniverseGlobalData(
                universeScienceData = copy(mutableUniverseScienceData)
            ),
        )
    }
}