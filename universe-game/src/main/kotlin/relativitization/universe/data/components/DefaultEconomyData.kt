package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.MutableSocialSecurityData
import relativitization.universe.data.components.defaults.economy.MutableTaxData
import relativitization.universe.data.components.defaults.economy.ResourceData
import relativitization.universe.data.components.defaults.economy.SocialSecurityData
import relativitization.universe.data.components.defaults.economy.TaxData

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

fun PlayerInternalData.economyData(): EconomyData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.economyData(): MutableEconomyData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.economyData(newEconomyData: MutableEconomyData) =
    playerDataComponentMap.put(newEconomyData)

