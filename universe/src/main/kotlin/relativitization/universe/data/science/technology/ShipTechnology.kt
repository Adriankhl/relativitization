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
    override val technologyField: TechnologyField = TechnologyField.MAX_SHIP_REST_MASS

    override val description: String = "Increase max ship rest mass by" +
            "$maxShipRestMassIncrease"

    override fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData) {
        mutableTechnologyData.shipEngineLevel += maxShipRestMassIncrease
    }
}

@Serializable
data class ShipEngineLevelTechnology(
    override val technologyId: Int,
    val shipEngineLevelIncrease: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleTechnologyData() {
    override val technologyField: TechnologyField = TechnologyField.SHIP_ENGINE_LEVEL

    override val description: String = "Increase max ship engine delta fuel rest mass by" +
            "$shipEngineLevelIncrease"

    override fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData) {
        mutableTechnologyData.shipEngineLevel += shipEngineLevelIncrease
    }
}