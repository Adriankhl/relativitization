package relativitization.universe.generate.method.random

import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData4D
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.generate.method.GenerateSettings

object RandomDefaultGenerate : RandomGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        return UniverseData(
            universeData4D = UniverseData4D(mutableListOf()),
            universeSettings = UniverseSettings(),
            universeState = UniverseState(0),
            commandMap = mutableMapOf(),
            universeGlobalData = UniverseGlobalData(),
        )
    }
}