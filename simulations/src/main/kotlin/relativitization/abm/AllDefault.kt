package relativitization.abm

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.maths.random.Rand

fun main() {
    // Set random seed
    Rand.setSeed(1L)

    val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")

    runBlocking {
        for (i in 1..10) {
            val aiCommandMap = universe.computeAICommands()

            universe.postProcessUniverse(
                mapOf(),
                aiCommandMap,
            )
            universe.preProcessUniverse()
        }
    }
}