package relativitization.universe.generate.method.random

import relativitization.universe.data.*
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableProjectGenerationData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.maths.grid.Grids

object RandomOneStarPerPlayerGenerate : RandomGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

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

        val mutableUniverseGlobalData = MutableUniverseGlobalData()

        // Add project generation data for all research field
        val mathematicsProjectGenerationData = MutableBasicResearchProjectGenerationData(
            basicResearchField = BasicResearchField.MATHEMATICS,
            projectGenerationData = MutableProjectGenerationData(
                centerX = 0.0,
                centerY = 0.0,
                range = 5.0,
                weight = 10.0,
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
                weight = 8.0,
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
                weight = 8.0,
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
                weight = 6.0,
            )
        )
        mutableUniverseGlobalData.universeScienceData().universeProjectGenerationData
            .basicResearchProjectGenerationDataList.add(lifeScienceProjectGenerationData)



        return UniverseData(
            universeData4D = DataSerializer.copy(mutableUniverseData4D),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = DataSerializer.copy((mutableUniverseGlobalData)),
        )
    }
}