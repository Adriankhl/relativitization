package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.economy.*

/**
 * @property taxData data about the tax rate of various stuff
 */
@Serializable
@SerialName("EconomyData")
data class EconomyData(
    val taxData: TaxData = TaxData(),
    val resourceData: ResourceData = ResourceData(),
    val socialSecurityData: SocialSecurityData = SocialSecurityData(),
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("EconomyData")
data class MutableEconomyData(
    var taxData: MutableTaxData = MutableTaxData(),
    var resourceData: MutableResourceData = MutableResourceData(),
    var socialSecurityData: MutableSocialSecurityData = MutableSocialSecurityData(),
) : MutableDefaultPlayerDataComponent()