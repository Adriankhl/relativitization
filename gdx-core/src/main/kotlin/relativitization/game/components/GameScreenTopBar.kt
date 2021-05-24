package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent


class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()

    val updatableByTopBar: MutableList<() -> Unit> = mutableListOf()

    private val serverStatusNameAndTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val serverUniverseTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val timeLeftLabel: Label = createLabel("", gdxSetting.smallFontSize)

    private val serverStatusTable: Table = createServerStatusLabels()

    private val universeDataSelectBox: SelectBox<String> = createSelectBox(
        game.universeClient.getAvailableData3DName(),
        "",
        gdxSetting.smallFontSize
    ) { name, _ ->
        game.universeClient.pickUniverseData3D(name)
    }

    private val updateButton: ImageButton = createImageButton(
        "basic/white-circle-arrow",
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        0.7f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        gdxSetting.soundEffectsVolume
    ) {
        update()
    }

    private val updateToLatestButton: ImageButton = createImageButton(
        "basic/white-rightmost-arrow",
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        0.7f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        gdxSetting.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.pickLatestUniverseData3D()
        }
        update()
    }

    init {
        // Set background color to blue
        table.background = assets.getBackgroundColor(0.1f, 0.1f, 0.1f, 1.0f)

        updateServerStatusLabels()

        table.add(serverStatusTable)

        table.add(updateButton).size(50f, 50f)

        table.add(updateToLatestButton).size(50f, 50f)
    }

    override fun get(): Table {
        return table
    }

    override fun update() {
        updateServerStatusLabels()
        updatableByTopBar.forEach { it() }
    }

    /**
     * Function that should be automatically call by universe client
     */
    fun autoUpdate() {
        updateServerStatusLabels()
    }

    private fun createServerStatusLabels(): Table {
        val nestedTable: Table = Table()
        nestedTable.add(serverStatusNameAndTimeLabel)

        nestedTable.row()

        nestedTable.add(serverUniverseTimeLabel)

        nestedTable.row()

        nestedTable.add(timeLeftLabel)

        return nestedTable
    }

    /**
     * Update the text label showing the server status
     */
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

        serverStatusNameAndTimeLabel.setText("Server status: ${serverStatus.universeName} ($connectionText)")

        serverUniverseTimeLabel.setText("Server universe time: ${serverStatus.currentUniverseTime}")

        timeLeftLabel.setText(
            "Input time left: $timeLeftText"
        )
    }

    /**
     * Update universeDataSelection Box
     */

}