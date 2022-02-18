package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
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

fun PlayerInternalData.economyData(): EconomyData =
    playerDataComponentMap.getOrDefault(EconomyData::class, EconomyData())

fun MutablePlayerInternalData.economyData(): MutableEconomyData =
    playerDataComponentMap.getOrDefault(MutableEconomyData::class, MutableEconomyData())

fun MutablePlayerInternalData.economyData(newEconomyData: MutableEconomyData) =
    playerDataComponentMap.put(newEconomyData)

