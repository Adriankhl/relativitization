package relativitization.universe.data.science.technology

data class EngineTechnology(
    override val technologyId: Int,
    val shipEngineTechnologyIncrease: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleTechnologyData() {
    override fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData) {
        mutableTechnologyData.shipEngineTechnology += shipEngineTechnologyIncrease
    }
}