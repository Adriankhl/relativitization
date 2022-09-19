package relativitization.universe.generate.random

import relativitization.universe.data.*
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.global.components.MutableDefaultGlobalDataComponent
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableAppliedResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableProjectGenerationData
import relativitization.universe.data.global.components.universeScienceData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.random.science.DefaultGenerateUniverseScienceData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.maths.grid.Grids
import relativitization.universe.mechanisms.defaults.dilated.pop.UpdateDesire
import relativitization.universe.mechanisms.defaults.regular.science.UpdateScienceApplicationData
import relativitization.universe.mechanisms.defaults.regular.sync.SyncPlayerScienceData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.floor
import kotlin.random.Random

object RandomOneStarPerPlayerGenerate : RandomGenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(
        settings: GenerateSettings,
        random: Random,
    ): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

        val mutableUniverseGlobalData = MutableUniverseGlobalData()

        // Add all default data component
        MutableDefaultGlobalDataComponent.createComponentList().forEach {
            mutableUniverseGlobalData.globalDataComponentMap.put(it)
        }

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
                random = random,
            )
        mutableUniverseGlobalData.universeScienceData(DataSerializer.copy(newUniverseScienceData))

        // Completed universe global data
        val universeGlobalData: UniverseGlobalData = DataSerializer.copy(mutableUniverseGlobalData)

        val mutableUniverseData4D = MutableUniverseData4D(
            Grids.create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableMapOf() }
        )

        // Only consider numPlayer, ignore numExtraStellarSystem
        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 0,
        )

        for (playerId in 1..settings.numPlayer) {
            val mutablePlayerData = MutablePlayerData(universeState.getNewPlayerId())

            MutableDefaultPlayerDataComponent.createComponentList().forEach {
                mutablePlayerData.playerInternalData.playerDataComponentMap.put(it)
            }

            // First n players are human player
            if (playerId <= settings.numHumanPlayer) {
                mutablePlayerData.playerType = PlayerType.HUMAN
            } else {
                mutablePlayerData.playerType = PlayerType.AI
            }

            // Random location, avoid too close to the boundary by adding a 0.1 width margin
            mutablePlayerData.double4D.x = random.nextDouble(
                0.1,
                universeSettings.xDim.toDouble() - 0.1
            )
            mutablePlayerData.double4D.y = random.nextDouble(
                0.1,
                universeSettings.yDim.toDouble() - 0.1
            )
            mutablePlayerData.double4D.z = random.nextDouble(
                0.1,
                universeSettings.zDim.toDouble() - 0.1
            )
            mutablePlayerData.int4D.x = floor(mutablePlayerData.double4D.x).toInt()
            mutablePlayerData.int4D.y = floor(mutablePlayerData.double4D.y).toInt()
            mutablePlayerData.int4D.z = floor(mutablePlayerData.double4D.z).toInt()

            // Add random stellar system
            mutablePlayerData.playerInternalData.popSystemData().addRandomStellarSystem(random)

            // Change initial population
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
                PopType.values().forEach { popType ->
                    carrier.allPopData.getCommonPopData(
                        popType
                    ).adultPopulation = settings.otherDoubleMap.getOrElse("initialPopulation") {
                        logger.debug("No initialPopulation variable, default to 1E6")
                        1E6
                    }
                }
            }

            // Add random basic and applied projects as done projects
            mutableUniverseGlobalData.universeScienceData().basicResearchProjectDataMap.values
                .shuffled(random).take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .doneBasicResearchProject(
                            it,
                            UpdateUniverseScienceData.basicResearchProjectFunction()
                        )
                }
            mutableUniverseGlobalData.universeScienceData().appliedResearchProjectDataMap.values
                .shuffled(random).take(5).forEach {
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
                }.shuffled(random).take(5).forEach {
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
                }.shuffled(random).take(5).forEach {
                    mutablePlayerData.playerInternalData.playerScienceData()
                        .knownAppliedResearchProject(
                            it,
                        )
                }

            // Add fuel to the player
            mutablePlayerData.playerInternalData.physicsData().fuelRestMassData.storage = 1E9

            // Use default mechanisms to update player data

            // Sync the science data (e.g., common sense)
            SyncPlayerScienceData.process(
                mutablePlayerData = mutablePlayerData,
                universeData3DAtPlayer = UniverseData3DAtPlayer(),
                universeSettings = universeSettings,
                universeGlobalData = universeGlobalData,
                random = random,
            )


            // Update science application data
            UpdateScienceApplicationData.process(
                mutablePlayerData = mutablePlayerData,
                universeData3DAtPlayer = UniverseData3DAtPlayer(),
                universeSettings = universeSettings,
                universeGlobalData = universeGlobalData,
                random = random,
            )

            // Update desire of pop
            UpdateDesire.process(
                mutablePlayerData = mutablePlayerData,
                universeData3DAtPlayer = UniverseData3DAtPlayer(),
                universeSettings = universeSettings,
                universeGlobalData = universeGlobalData,
                random = random,
            )

            mutablePlayerData.syncData()
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
            universeGlobalData = universeGlobalData,
        )
    }
}