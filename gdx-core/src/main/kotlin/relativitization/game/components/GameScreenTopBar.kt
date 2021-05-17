package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()

    private val universeNameAndTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val waitingInputAndTimeLeftLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val connectionSuccessLabel: Label = createLabel("", gdxSetting.smallFontSize)

    init {
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

        nestedTable.row().space(10f)

        nestedTable.add(waitingInputAndTimeLeftLabel)

        nestedTable.row().space(10f)

        nestedTable.add(connectionSuccessLabel)

        return nestedTable
    }

    private fun updateServerStatusLabels() {
        val serverStatus = game.universeClient.getServerStatus()
        universeNameAndTimeLabel.setText("${serverStatus.universeName}: ${serverStatus.currentUniverseTime}")
        waitingInputAndTimeLeftLabel.setText("Waiting input: ${serverStatus.waitingInput}, time left: ${serverStatus.timeLeft / 1000} s")
    }
}