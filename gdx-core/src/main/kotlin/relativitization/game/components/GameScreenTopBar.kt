package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent


class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()

    private val universeNameAndTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val serverStatusLabel: Label = createLabel("", gdxSetting.smallFontSize)

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

        nestedTable.add(serverStatusLabel)

        return nestedTable
    }

    private fun updateServerStatusLabels() {
        // copy to prevent change
        val serverStatus = game.universeClient.getServerStatus().copy()

        val connectionText = if (serverStatus.success) {
            "connected"
        } else {
            "disconnected"
        }

        val timeLeftText = if (serverStatus.waitingInput) {
            "${serverStatus.timeLeft / 1000} s"
        } else {
            "waiting data"
        }

        universeNameAndTimeLabel.setText("${serverStatus.universeName} ($connectionText) - ${serverStatus.currentUniverseTime}")

        serverStatusLabel.setText(
            "Time left: $timeLeftText"
        )
    }
}