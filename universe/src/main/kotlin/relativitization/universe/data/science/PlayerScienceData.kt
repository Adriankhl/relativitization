package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.science.knowledge.KnowledgeData
import relativitization.universe.data.science.knowledge.MutableKnowledgeData
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.MutableTechnologyData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.science.technology.TechnologyData
import relativitization.universe.data.serializer.DataSerializer

@Serializable
data class PlayerScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val commonSenseTechnologyData: TechnologyData = TechnologyData(),
    val singleKnowledgeDataList: List<SingleKnowledgeData> = listOf(),
    val singleTechnologyDataList: List<SingleTechnologyData> = listOf(),
    val playerKnowledgeData: KnowledgeData = KnowledgeData(),
    val playerTechnologyData: TechnologyData = TechnologyData(),
)

@Serializable
data class MutablePlayerScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var commonSenseTechnologyData: MutableTechnologyData = MutableTechnologyData(),
    val singleKnowledgeDataList: MutableList<SingleKnowledgeData> = mutableListOf(),
    val singleTechnologyDataList: MutableList<SingleTechnologyData> = mutableListOf(),
    var playerKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var playerTechnologyData: MutableTechnologyData = MutableTechnologyData(),
) {
    /**
     * Compute player knowledge data by common sense and knowledge data list
     */
    fun computePlayerKnowledgeData() {
        playerKnowledgeData = DataSerializer.copy(commonSenseKnowledgeData)
        singleKnowledgeDataList.forEach {
            it.updateKnowledgeData(playerKnowledgeData)
        }
    }

    /**
     * Compute player technology data by common sense and technology data list
     */
    fun computePlayerTechnologyData() {
        playerTechnologyData = DataSerializer.copy(commonSenseTechnologyData)
        singleTechnologyDataList.forEach {
            it.updateTechnologyData(playerTechnologyData)
        }
    }

    /**
     * Add Single knowledge data
     */
    fun addSingleKnowledgeData(singleKnowledgeData: SingleKnowledgeData) {
        singleKnowledgeDataList.add(singleKnowledgeData)
        singleKnowledgeData.updateKnowledgeData(playerKnowledgeData)
    }

    /**
     * Add single technology data
     */
    fun addSingleTechnologyData(singleTechnologyData: SingleTechnologyData) {
        singleTechnologyDataList.add(singleTechnologyData)
        singleTechnologyData.updateTechnologyData(playerTechnologyData)
    }
}