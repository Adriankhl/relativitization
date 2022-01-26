package relativitization.game.utils

import relativitization.universe.data.components.defaults.popsystem.CarrierData

object Summary {
    fun computeCarrierListSummary(
        carrierList: List<CarrierData>
    ): CarrierListSummary {

        return CarrierListSummary(
            numCarrier = carrierList.size,
        )
    }
}

data class CarrierListSummary(
    val numCarrier: Int,
)