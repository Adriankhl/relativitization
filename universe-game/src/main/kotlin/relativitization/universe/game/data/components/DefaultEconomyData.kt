package relativitization.universe.game.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.game.data.components.defaults.economy.MutableResourceData
import relativitization.universe.game.data.components.defaults.economy.MutableSocialSecurityData
import relativitization.universe.game.data.components.defaults.economy.MutableTaxData
import relativitization.universe.game.data.components.defaults.economy.ResourceData
import relativitization.universe.game.data.components.defaults.economy.SocialSecurityData
import relativitization.universe.game.data.components.defaults.economy.TaxData

/**
 * @property taxData data about the tax rate of various stuff
 */
@GenerateImmutable
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

