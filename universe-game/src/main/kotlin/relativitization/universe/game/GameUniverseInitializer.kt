package relativitization.universe.game

import ksergen.GeneratedModule
import relativitization.universe.core.RelativitizationInitializer
import relativitization.universe.game.ai.DefaultAI
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.generate.random.RandomHierarchyGenerate
import relativitization.universe.game.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists

object GameUniverseInitializer {
    fun initialize() {
        RelativitizationInitializer.initialize(
            serializersModule = GeneratedModule.serializersModule,
            mechanismLists = DefaultMechanismLists,
            globalMechanismList = DefaultGlobalMechanismList,
            ai = DefaultAI,
            commandAvailability = DefaultCommandAvailability,
        )

        RelativitizationInitializer.initialize(
            generateUniverseMethodList = listOf(
                RandomOneStarPerPlayerGenerate,
                RandomHierarchyGenerate,
                TestingFixedMinimalGenerate,
            )
        )
    }
}