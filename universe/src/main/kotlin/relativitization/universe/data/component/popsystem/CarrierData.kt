package relativitization.universe.data.component.popsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.AllPopData
import relativitization.universe.data.component.popsystem.pop.MutableAllPopData

@Serializable
data class CarrierData(
    val coreRestMass: Double = 1.0,
    val maxDeltaFuelRestMass: Double = 0.0,
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val allPopData: AllPopData = AllPopData(),
    val combatData: CombatData = CombatData(),

)

@Serializable
data class MutableCarrierData(
    var coreRestMass: Double = 1.0,
    var maxDeltaFuelRestMass: Double = 0.0,
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var allPopData: MutableAllPopData = MutableAllPopData(),
    var combatData: MutableCombatData = MutableCombatData(),
)

enum class CarrierType {
    STELLAR,
    PLANETARY,
    SPACESHIP
}