package relativitization.game

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name

fun main() {
    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = 50,
        numHumanPlayer = 1,
        numExtraStellarSystem = 0,
        initialPopulation = 1E6,
        universeSettings = MutableUniverseSettings(
            universeName = "Typical game",
            mechanismCollectionName = DefaultMechanismLists.name(),
            commandCollectionName = DefaultCommandAvailability.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            tDim = 16,
            xDim = 10,
            yDim = 10,
            zDim = 3,
            playerAfterImageDuration = 4,
            playerHistoricalInt4DLength = 4,
            groupEdgeLength = 0.01,
            otherSettings = mutableMapOf(),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting), ".")


    runBlocking {
        for (turn in 1..100) {
            val aiCommandMap = universe.computeAICommands()

            universe.postProcessUniverse(
                mapOf(),
                aiCommandMap
            )
            universe.preProcessUniverse()
        }
    }
}