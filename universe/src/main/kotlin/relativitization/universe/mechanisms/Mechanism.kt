package relativitization.universe.mechanisms

import org.apache.logging.log4j.core.tools.picocli.CommandLine
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer

abstract class Mechanism {
    abstract fun process(mutablePlayerData: MutablePlayerData, universeData3DAtPlayer: UniverseData3DAtPlayer): List<CommandLine.Command>
}

object AllMechanism {
    val mechanismList: List<Mechanism> = listOf()
}