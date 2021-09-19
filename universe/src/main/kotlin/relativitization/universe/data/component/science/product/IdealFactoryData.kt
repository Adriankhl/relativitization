package relativitization.universe.data.component.science.product

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData

@Serializable
data class IdealFactoryData(
    val resourceQualityData: ResourceQualityData,
)

@Serializable
data class MutableIdealFactoryData(
    var resourceQualityData: MutableResourceQualityData,
)