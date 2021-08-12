package relativitization.universe.data.economy.resource

import kotlinx.serialization.Serializable

@Serializable
data class ResourceStockData(
    val resourceData: ResourceData = ResourceData(),
    val amount: Double = 0.0,
)

@Serializable
data class MutableResourceStockData(
    var resourceData: MutableResourceData = MutableResourceData(),
    var amount: Double = 0.0,
)

@Serializable
data class ResourceMarketData(
    val resourceStockData: ResourceStockData = ResourceStockData(),
    val price: Double = 1.0,
)

@Serializable
data class MutableResourceMarketData(
    var resourceStockData: MutableResourceStockData = MutableResourceStockData(),
    var price: Double = 1.0,
)

@Serializable
data class AllClassResourceStockData(
    val type: ResourceType = ResourceType.PLANT,
    val firstClassQuality: ResourceQualityData = ResourceQualityData(),
    val firstClassAmount: Double = 0.0,
    val secondClassQuality: ResourceQualityData = ResourceQualityData(),
    val secondClassAmount: Double = 0.0,
    val thirdClassQuality: ResourceQualityData = ResourceQualityData(),
    val thirdClassAmount: Double = 0.0,
)

@Serializable
data class MutableAllClassResourceStockData(
    var type: ResourceType = ResourceType.PLANT,
    var firstClassQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var firstClassAmount: Double = 0.0,
    var secondClassQuality: MutableResourceQualityData = MutableResourceQualityData(), var secondClassAmount: Double = 0.0,
    var thirdClassQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var thirdClassAmount: Double = 0.0,
)

@Serializable
data class AllClassResourceMarketData(
    val allClassResourceStockData: AllClassResourceStockData = AllClassResourceStockData(),
    val firstClassPrice: Double = 1.0,
    val secondClassPrice: Double = 1.0,
    val thirdClassPrice: Double = 1.0,
)

@Serializable
data class AllClassMutableResourceMarketData(
    var allClassResourceStockData: MutableAllClassResourceStockData = MutableAllClassResourceStockData(),
    var firstClassPrice: Double = 1.0,
    var secondClassPrice: Double = 1.0,
    var thirdClassPrice: Double = 1.0,
)