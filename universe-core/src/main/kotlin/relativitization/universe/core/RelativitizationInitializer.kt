package relativitization.universe.core

import kotlinx.serialization.modules.SerializersModule
import relativitization.universe.core.ai.AI
import relativitization.universe.core.ai.AICollection
import relativitization.universe.core.ai.EmptyAI
import relativitization.universe.core.data.commands.AllCommandAvailability
import relativitization.universe.core.data.commands.CommandAvailability
import relativitization.universe.core.data.commands.CommandCollection
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.generate.GenerateUniverseMethod
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.generate.empty.EmptyUniverse
import relativitization.universe.core.global.EmptyGlobalMechanismList
import relativitization.universe.core.global.GlobalMechanismCollection
import relativitization.universe.core.global.GlobalMechanismList
import relativitization.universe.core.mechanisms.EmptyMechanismLists
import relativitization.universe.core.mechanisms.MechanismCollection
import relativitization.universe.core.mechanisms.MechanismLists
import relativitization.universe.core.spacetime.MinkowskiSpacetime
import relativitization.universe.core.spacetime.Spacetime
import relativitization.universe.core.spacetime.SpacetimeCollection

object RelativitizationInitializer {
    /**
     * Add objects (defined in a model or a game) to the framework
     */
    fun initialize(
        serializersModule: SerializersModule = DataSerializer.getJsonFormat().serializersModule,
        generateUniverseMethod: GenerateUniverseMethod = EmptyUniverse,
        mechanismLists: MechanismLists = EmptyMechanismLists,
        globalMechanismList: GlobalMechanismList = EmptyGlobalMechanismList,
        ai: AI = EmptyAI,
        commandAvailability: CommandAvailability = AllCommandAvailability,
        spacetime: Spacetime = MinkowskiSpacetime,
    ) {
        initializeSerializersModule(serializersModule)
        initializeGenerateUniverseMethod(generateUniverseMethod)
        initializeMechanismLists(mechanismLists)
        initializeGlobalMechanismList(globalMechanismList)
        initializeAI(ai)
        initializeCommandAvailability(commandAvailability)
        initializeSpacetime(spacetime)
    }

    /**
     * Add lists of objects (defined in a model or a game) to the framework
     */
    fun initialize(
        serializersModuleList: List<SerializersModule> = emptyList(),
        generateUniverseMethodList: List<GenerateUniverseMethod> = emptyList(),
        mechanismListsList: List<MechanismLists> = emptyList(),
        globalMechanismListList: List<GlobalMechanismList> = emptyList(),
        aiList: List<AI> = emptyList(),
        commandAvailabilityList: List<CommandAvailability> = emptyList(),
        spacetimeList: List<Spacetime> = emptyList(),
    ) {
        serializersModuleList.forEach { serializersModule ->
            initializeSerializersModule(serializersModule)
        }
        generateUniverseMethodList.forEach { generateUniverseMethod ->
            initializeGenerateUniverseMethod(generateUniverseMethod)
        }
        mechanismListsList.forEach { mechanismLists ->
            initializeMechanismLists(mechanismLists)
        }
        globalMechanismListList.forEach { globalMechanismList ->
            initializeGlobalMechanismList(globalMechanismList)
        }
        aiList.forEach { ai ->
            initializeAI(ai)
        }
        commandAvailabilityList.forEach { commandAvailability ->
            initializeCommandAvailability(commandAvailability)
        }
        spacetimeList.forEach { spacetime ->
            initializeSpacetime(spacetime)
        }
    }

    private fun initializeSerializersModule(serializersModule: SerializersModule) {
        if (serializersModule != DataSerializer.getJsonFormat().serializersModule) {
            DataSerializer.updateJsonFormatModule(serializersModule)
        }
    }

    private fun initializeGenerateUniverseMethod(generateUniverseMethod: GenerateUniverseMethod) {
        if (generateUniverseMethod !is EmptyUniverse) {
            GenerateUniverseMethodCollection.addGenerateUniverseMethod(generateUniverseMethod)
        }
    }

    private fun initializeMechanismLists(mechanismLists: MechanismLists) {
        if (mechanismLists !is EmptyMechanismLists) {
            MechanismCollection.addMechanismLists(mechanismLists)
        }
    }


    private fun initializeGlobalMechanismList(globalMechanismList: GlobalMechanismList) {
        if (globalMechanismList !is EmptyGlobalMechanismList) {
            GlobalMechanismCollection.addGlobalMechanismList(globalMechanismList)
        }
    }

    private fun initializeAI(ai: AI) {
        if (ai !is EmptyAI) {
            AICollection.addAI(ai)
        }
    }

    private fun initializeCommandAvailability(commandAvailability: CommandAvailability) {
        if (commandAvailability !is AllCommandAvailability) {
            CommandCollection.addCommandAvailability(commandAvailability)
        }
    }

    private fun initializeSpacetime(spacetime: Spacetime) {
        if (spacetime !is MinkowskiSpacetime) {
            SpacetimeCollection.addSpacetime(spacetime)
        }
    }
}