package relativitization.universe.data.science.technology

import kotlinx.serialization.Serializable


@Serializable
data class MaxShipRestMassTechnology(
    override val technologyId: Int,
    val maxShipRestMassIncrease: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleTechnologyData() {
    override val description: String = "Increase max ship rest mass by" +
            "$maxShipRestMassIncrease"

    override fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData) {
        mutableTechnologyData.shipEngineLevel += maxShipRestMassIncrease
    }
}

@Serializable
data class MaxShipEngineDeltaFuelRestMassTechnology(
    override val technologyId: Int,
    val maxShipEngineDeltaFuelRestMassIncrease: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleTechnologyData() {
    override val description: String = "Increase max ship engine delta fuel rest mass by" +
            "$maxShipEngineDeltaFuelRestMassIncrease"

    override fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData) {
        mutableTechnologyData.shipEngineLevel += maxShipEngineDeltaFuelRestMassIncrease
    }
}