package relativitization.universe.core.global

import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.global.MutableUniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

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
        random: Random,
    )
}

abstract class GlobalMechanismList {
    abstract val globalMechanismList: List<GlobalMechanism>

    open fun name(): String = this::class.simpleName.toString()
}

object GlobalMechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val globalMechanismListNameMap: MutableMap<String, GlobalMechanismList> = mutableMapOf(
        EmptyGlobalMechanismList.name() to EmptyGlobalMechanismList,
    )

    fun getGlobalMechanismListNames(): Set<String> = globalMechanismListNameMap.keys

    fun addGlobalMechanismList(globalMechanismList: GlobalMechanismList) {
        val globalMechanismListName: String = globalMechanismList.name()
        if (globalMechanismListNameMap.containsKey(globalMechanismListName)) {
            logger.debug(
                "Already has $globalMechanismListName in GlobalMechanismCollection, " +
                        "replacing stored $globalMechanismListName"
            )
        }

        globalMechanismListNameMap[globalMechanismListName] = globalMechanismList
    }

    fun globalProcess(
        universeData: UniverseData,
        random: Random,
    ) {
        val mutableUniverseGlobalData: MutableUniverseGlobalData =
            DataSerializer.copy(universeData.universeGlobalData)

        globalMechanismListNameMap.getOrElse(universeData.universeSettings.globalMechanismCollectionName) {
            logger.error("No global mechanism name matched, use empty mechanism")
            EmptyGlobalMechanismList
        }.globalMechanismList.forEach { globalMechanism ->
            globalMechanism.updateGlobalData(
                mutableUniverseGlobalData = mutableUniverseGlobalData,
                universeData = universeData,
                random = random,
            )
        }

        universeData.universeGlobalData = DataSerializer.copy(mutableUniverseGlobalData)
    }
}