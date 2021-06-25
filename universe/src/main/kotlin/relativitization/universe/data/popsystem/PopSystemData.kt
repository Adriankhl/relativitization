package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class PopSystemicData(
    val carrier: List<Carrier> = listOf()
)

@Serializable
data class MutablePopSystemicData(
    val carrier: MutableList<MutableCarrier> = mutableListOf()
) {
    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        carrier.add(MutableCarrier(restMass, CarrierType.STELLAR))
    }
}