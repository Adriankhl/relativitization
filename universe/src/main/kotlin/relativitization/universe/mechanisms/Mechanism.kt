package relativitization.universe.mechanisms

import org.apache.logging.log4j.core.tools.picocli.CommandLine
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

abstract class Mechanism {
    abstract fun process(mutablePlayerData: MutablePlayerData, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}

object MechanismCollection {
    val mechanismList: List<Mechanism> = listOf()
}