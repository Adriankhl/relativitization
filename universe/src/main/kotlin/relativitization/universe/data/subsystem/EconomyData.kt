package relativitization.universe.data.subsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.economy.*

/**
 * @property taxRateData data about the tax rate of various stuff
 */
@Serializable
data class EconomyData(
    val taxRateData: TaxRateData = TaxRateData(),
    val resourceData: ResourceData = ResourceData(),
    val tradeHistoryData: TradeHistoryData = TradeHistoryData(),
    val socialSecurityData: SocialSecurityData = SocialSecurityData(),
) : PlayerSubsystemData

@Serializable
data class MutableEconomyData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var resourceData: MutableResourceData = MutableResourceData(),
    var tradeHistoryData: MutableTradeHistoryData = MutableTradeHistoryData(),
    var socialSecurityData: MutableSocialSecurityData = MutableSocialSecurityData(),
) : MutablePlayerSubsystemData