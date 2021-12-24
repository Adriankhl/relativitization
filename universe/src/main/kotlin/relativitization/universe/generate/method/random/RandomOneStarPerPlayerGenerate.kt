package relativitization.universe.generate.method.random

import relativitization.universe.data.*
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableAppliedResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableProjectGenerationData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.science.DefaultGenerateUniverseScienceData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.maths.grid.Grids
import relativitization.universe.maths.random.Rand

object RandomOneStarPerPlayerGenerate : RandomGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

        val mutableUniverseGlobalData = MutableUniverseGlobalData()

        // Add project generation data for all basic research field
        val mathematicsProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.MATHEMATICS,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = 0.0,
                range = 5.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(mathematicsProjectGenerationData)

        val physicsProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.PHYSICS,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 2.0,
                centerY = 0.0,
                range = 5.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(physicsProjectGenerationData)

        val computerScienceProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.COMPUTER_SCIENCE,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = 2.0,
                range = 5.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(computerScienceProjectGenerationData)

        val lifeScienceProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.LIFE_SCIENCE,
            projectGenerationData = MutableProjectGenerationData(
                centerX = -2.0,
                centerY = 0.0,
                range = 4.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(lifeScienceProjectGenerationData)


        val socialScienceProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.SOCIAL_SCIENCE,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 1.0,
                centerY = -2.0,
                range = 4.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(socialScienceProjectGenerationData)

        val humanityProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.HUMANITY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = -2.0,
                range = 4.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(humanityProjectGenerationData)

        // Add project generation data for all applied research field
        val energyProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = 0.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(energyProjectGenerationData)

        val foodProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.FOOD_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = -3.0,
                centerY = 0.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(foodProjectGenerationData)

        val biomedicalProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.BIOMEDICAL_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = -3.0,
                centerY = 3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(biomedicalProjectGenerationData)

        val chemicalProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.BIOMEDICAL_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 3.0,
                centerY = 3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(chemicalProjectGenerationData)

        val environmentalProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.ENVIRONMENTAL_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 3.0,
                centerY = -3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(environmentalProjectGenerationData)

        val architectureProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.ARCHITECTURE_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = -3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(architectureProjectGenerationData)

        val machineryProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.ARCHITECTURE_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 3.0,
                centerY = 0.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(machineryProjectGenerationData)

        val materialProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.MATERIAL_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 3.0,
                centerY = 1.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(materialProjectGenerationData)

        val informationProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.INFORMATION_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = 3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(informationProjectGenerationData)

        val artProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.ART_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 1.0,
                centerY = -3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(artProjectGenerationData)


        val militaryProjectGenerationData = MutableAppliedResearchProjectGenerationData(
            appliedResearchField = AppliedResearchField.MILITARY_TECHNOLOGY,
            projectGenerationData = MutableProjectGenerationData(
                centerX = -3.0,
                centerY = -3.0,
                range = 3.0,
                weight = 1.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .appliedResearchProjectGenerationDataList.add(militaryProjectGenerationData)

        // Generate science projects and replace the universe science data
        val newUniverseScienceData: UniverseScienceData =
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData = DataSerializer.copy(
                    mutableUniverseGlobalData.universeScienceData()
                ),
                numBasicResearchProjectGenerate = 30,
                numAppliedResearchProjectGenerate = 30,
                maxBasicReference = 10,
                maxAppliedReference = 10,
                maxDifficulty = 1.0,
                maxSignificance = 1.0,
            )
        mutableUniverseGlobalData.universeScienceData(DataSerializer.copy(newUniverseScienceData))

        val mutableUniverseData4D = MutableUniverseData4D(
            Grids.create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        // Only consider numPlayer, ignore numExtraStellarSystem
        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = settings.numPlayer,
        )

        for (playerId in 1..settings.numPlayer) {
            val mutablePlayerData = MutablePlayerData(playerId)

            // Random location
            mutablePlayerData.int4D.x = Rand.rand().nextInt(0, universeSettings.xDim)
            mutablePlayerData.int4D.y = Rand.rand().nextInt(0, universeSettings.yDim)
            mutablePlayerData.int4D.z = Rand.rand().nextInt(0, universeSettings.zDim)

            // Add random stellar system
            mutablePlayerData.playerInternalData.popSystemData().addRandomStellarSystem()

            // Add random basic and applied projects as done projects
            mutableUniverseGlobalData.universeScienceData().basicResearchProjectDataMap.values
                .shuffled().take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .doneBasicResearchProject(
                            it,
                            UpdateUniverseScienceData.basicResearchProjectFunction()
                        )
                }
            mutableUniverseGlobalData.universeScienceData().appliedResearchProjectDataMap.values
                .shuffled().take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .doneAppliedResearchProject(
                            it,
                            UpdateUniverseScienceData.appliedResearchProjectFunction()
                        )
                }

            // Add random basic and applied projects as known projects
            mutableUniverseGlobalData.universeScienceData().basicResearchProjectDataMap.values
                .filter { universeProject ->
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .doneBasicResearchProjectList.all {
                            it.basicResearchId != universeProject.basicResearchId
                        }
                }.shuffled().take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .knownBasicResearchProject(
                            it,
                        )
                }
            mutableUniverseGlobalData.universeScienceData().appliedResearchProjectDataMap.values
                .filter { universeProject ->
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .doneAppliedResearchProjectList.all {
                            it.appliedResearchId != universeProject.appliedResearchId
                        }
                }.shuffled().take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .knownAppliedResearchProject(
                            it,
                        )
                }

            // Add fuel to the player
            mutablePlayerData.playerInternalData.physicsData().targetFuelRestMassData.storage = 1E9
            mutablePlayerData.playerInternalData.physicsData().addFuel(5E7)

            mutableUniverseData4D.addPlayerDataToLatestWithAfterImage(
                mutablePlayerData = mutablePlayerData,
                currentTime = universeState.getCurrentTime(),
                edgeLength = universeSettings.groupEdgeLength,
                playerAfterImageDuration = universeSettings.playerAfterImageDuration
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(mutableUniverseData4D),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = DataSerializer.copy((mutableUniverseGlobalData)),
        )
    }
}