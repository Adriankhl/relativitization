package relativitization.universe.core.generate.empty

import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseData4D
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.UniverseState
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethod
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