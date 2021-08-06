package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import kotlin.math.max
import kotlin.math.min

class ScienceInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val knowledgeBar: Table = Table()

    private val knowledgeGroup: Group = Group()

    private val knowledgeGroupScrollPane: ScrollPane = createScrollPane(knowledgeGroup)

    private val table: Table = Table()

    // the currently viewing player data
    private var playerData: PlayerData = PlayerData(-1)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        updateTable()
    }

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        updateKnowledgeBar()

        updateKnowledgeGroup()

        table.add(knowledgeBar)

        table.row().space(20f)

        table.add(knowledgeGroupScrollPane)

        table.row().space(20f)
    }

    private fun updateKnowledgeBar() {
        knowledgeBar.clear()

        val headerLabel = createLabel("Science: player ${playerData.id}", gdxSettings.bigFontSize)

        knowledgeBar.add(headerLabel)
    }

    private fun updateKnowledgeGroup() {
        knowledgeGroup.clear()

        knowledgeGroup.setSize(
            knowledgeMapWidth().toFloat() * actualZoom(),
            knowledgeMapHeight().toFloat() * actualZoom()
        )

        for (i in 1..50) {
            val testLabel = createLabel("test$i", gdxSettings.bigFontSize)

            testLabel.y = i * 50f

            knowledgeGroup.addActor(testLabel)
        }
    }

    /**
     * Compute the width of the knowledge map
     */
    private fun knowledgeMapWidth(): Double {

        // Compute the dimension of knowledge map
        val minBasicX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.xCor
        } ?: -1.0
        val minAppliedX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.xCor
        } ?: -1.0
        val maxBasicX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.xCor
        } ?: 1.0
        val maxAppliedX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.xCor
        } ?: 1.0

        return max(maxBasicX, maxAppliedX) - min(minBasicX, minAppliedX)
    }


    /**
     * Compute the height of the knowledge map
     */
    private fun knowledgeMapHeight(): Double {

        // Compute the dimension of knowledge map
        val minBasicY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.yCor
        } ?: -1.0
        val minAppliedY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.yCor
        } ?: -1.0
        val maxBasicY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.yCor
        } ?: 1.0
        val maxAppliedY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.yCor
        } ?: 1.0

        return max(maxBasicY, maxAppliedY) - min(minBasicY, minAppliedY)
    }

    private fun actualZoom(): Float {
        // Actual zoom when mapZoomRelativeToFullMap equals 1.0
        val zoomOne = min(
            knowledgeGroupScrollPane.width / knowledgeMapWidth().toFloat(),
            knowledgeGroupScrollPane.height / knowledgeMapHeight().toFloat()
        )
        return zoomOne * gdxSettings.knowledgeMapZoomRelativeToFullMap
    }
}