package relativitization.game.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent


class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()

    private val universeNameAndTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val waitingInputAndTimeLeftLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val connectionSuccessLabel: Label = createLabel("", gdxSetting.smallFontSize)

    init {
        // Set background color to blue
        table.background = assets.getBackGroundColor(0.2f, 0.3f, 0.5f, 1.0f)

        table.add(addServerStatusLabels())
    }

    override fun get(): Table {
        return table
    }

    override fun update() {
        updateServerStatusLabels()
    }

    private fun addServerStatusLabels(): Table {
        val nestedTable: Table = Table()
        nestedTable.add(universeNameAndTimeLabel)

        nestedTable.row()

        nestedTable.add(waitingInputAndTimeLeftLabel)

        nestedTable.row()

        nestedTable.add(connectionSuccessLabel)

        return nestedTable
    }

    private fun updateServerStatusLabels() {
        // copy to prevent change
        val serverStatus = game.universeClient.getServerStatus().copy()
        universeNameAndTimeLabel.setText("${serverStatus.universeName}, time ${serverStatus.currentUniverseTime}")
        waitingInputAndTimeLeftLabel.setText("Waiting input: ${serverStatus.waitingInput}, input time left: ${serverStatus.timeLeft / 1000} s")
        connectionSuccessLabel.setText("Connection: ${serverStatus.success}")
    }
}