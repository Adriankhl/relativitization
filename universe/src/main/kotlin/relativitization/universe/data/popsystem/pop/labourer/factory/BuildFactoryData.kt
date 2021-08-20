package relativitization.universe.data.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.MutableResourceQualityData
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType

/**
 * A factory waiting to be built
 *
 * @property outputResource the output resource of the factory
 * @property targetQualityData the target output resource quality
 * @property progress the progress of building, from 0 and 1
 */
@Serializable
data class BuildFactoryData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val targetQualityData: ResourceQualityData = ResourceQualityData(),
    val progress: Double = 0.0,
)

@Serializable
data class MutableBuildFactoryData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var targetQualityData: MutableResourceQualityData = MutableResourceQualityData()
    var progress: Double = 0.0,
)