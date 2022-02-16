package relativitization.universe.global

import relativitization.universe.data.UniverseData
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

abstract class GlobalMechanism {
    /**
     * Update global data
     *
     * @param mutableUniverseGlobalData the global data to be update
     * @param universeData the update process can depend on this universe data
     */
    abstract fun updateGlobalData(
        mutableUniverseGlobalData: MutableUniverseGlobalData,
        universeData: UniverseData,
    )
}

sealed class GlobalMechanismList {
    abstract val globalMechanismList: List<GlobalMechanism>
}

fun GlobalMechanismList.name(): String = this::class.simpleName.toString()

object GlobalMechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val globalMechanismListList: List<GlobalMechanismList> =
        GlobalMechanismList::class.sealedSubclasses.map { it.objectInstance!! }

    val globalMechanismListMap: Map<String, GlobalMechanismList> = globalMechanismListList.associateBy {
        it.name()
    }

    fun globalProcess(
        universeData: UniverseData
    ) {
        val mutableUniverseGlobalData: MutableUniverseGlobalData =
            DataSerializer.copy(universeData.universeGlobalData)

        globalMechanismListMap.getOrElse(universeData.universeSettings.globalMechanismCollectionName) {
            logger.error("No global mechanism name matched, use empty mechanism")
            EmptyGlobalMechanismList
        }.globalMechanismList.forEach { globalMechanism ->
            globalMechanism.updateGlobalData(mutableUniverseGlobalData, universeData)
        }

        universeData.universeGlobalData = DataSerializer.copy(mutableUniverseGlobalData)
    }
}