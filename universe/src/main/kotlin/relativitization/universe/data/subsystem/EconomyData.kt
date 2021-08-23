package relativitization.universe.data.subsystem

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.economy.*

/**
 * @property taxRateData data about the tax rate of various stuff
 */
@Serializable
@SerialName("EconomyData")
data class EconomyData(
    val taxRateData: TaxRateData = TaxRateData(),
    val resourceData: ResourceData = ResourceData(),
    val tradeHistoryData: TradeHistoryData = TradeHistoryData(),
    val socialSecurityData: SocialSecurityData = SocialSecurityData(),
) : PlayerSubsystemData()

@Serializable
@SerialName("EconomyData")
data class MutableEconomyData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var resourceData: MutableResourceData = MutableResourceData(),
    var tradeHistoryData: MutableTradeHistoryData = MutableTradeHistoryData(),
    var socialSecurityData: MutableSocialSecurityData = MutableSocialSecurityData(),
) : MutablePlayerSubsystemData()