package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.*

/**
 * @property taxData data about the tax rate of various stuff
 */
@Serializable
@SerialName("EconomyData")
data class EconomyData(
    val taxData: TaxData = TaxData(),
    val resourceData: ResourceData = ResourceData(),
    val tradeHistoryData: TradeHistoryData = TradeHistoryData(),
    val socialSecurityData: SocialSecurityData = SocialSecurityData(),
) : PlayerDataComponent()

@Serializable
@SerialName("EconomyData")
data class MutableEconomyData(
    var taxData: MutableTaxData = MutableTaxData(),
    var resourceData: MutableResourceData = MutableResourceData(),
    var tradeHistoryData: MutableTradeHistoryData = MutableTradeHistoryData(),
    var socialSecurityData: MutableSocialSecurityData = MutableSocialSecurityData(),
) : MutablePlayerDataComponent()