package relativitization.universe.data.component.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType

/**
 * A factory waiting to be built
 *
 * @property outputResource the output resource of the factory
 * @property targetQualityData the target output resource quality
 * @property progress the progress of building, from 0 and 1
 */
@Serializable
data class BuildFactoryData(
    val ownerPlayerId: Int = -1,
    val outputResource: ResourceType = ResourceType.FUEL,
    val targetQualityData: ResourceQualityData = ResourceQualityData(),
    val targetOutputAmount: Double = 0.0,
    val progress: Double = 0.0,
)

@Serializable
data class MutableBuildFactoryData(
    var ownerPlayerId: Int = -1,
    var outputResource: ResourceType = ResourceType.FUEL,
    var targetQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var targetOutputAmount: Double = 0.0,
    var progress: Double = 0.0,
)