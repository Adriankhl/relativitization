package relativitization.abm

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection

fun main() {
    val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")

    runBlocking {
        for (i in 1..10) {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }
    }
}