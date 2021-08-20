package relativitization.universe.data.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType

/**
 * A factory waiting to be built
 *
 * @property outputResource the output resource of the factory
 * @property qualityScale how good is the output resource quality in a scale between 0 and 1
 * @property progress the progress of building, from 0 and 1
 */
@Serializable
data class BuildFactoryData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val qualityScale: Double = 0.0,
    val progress: Double = 0.0,
)

@Serializable
data class MutableBuildFactoryData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var qualityScale: Double = 0.0,
    var progress: Double = 0.0,
)