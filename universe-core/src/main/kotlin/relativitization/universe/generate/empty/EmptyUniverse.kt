package relativitization.universe.generate.empty

import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData4D
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethod
import kotlin.random.Random

object EmptyUniverse : GenerateUniverseMethod() {
    override fun generate(generateSettings: GenerateSettings, random: Random): UniverseData {
        return UniverseData(
            UniverseData4D(mutableListOf()),
            UniverseSettings(),
            UniverseState(0),
            mutableMapOf(),
            UniverseGlobalData(),
        )
    }
}