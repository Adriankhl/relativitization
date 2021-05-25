package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
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

    private val serverStatusTable: Table = createServerStatusTable()

    private val currentUniverseDataLabel: Label = createLabel("", gdxSetting.smallFontSize)

    // button to select previous time
    private val previousButton: ImageButton = createImageButton(
        "basic/white-left-arrow",
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
        game.universeClient.previousUniverseData3D()
        updateAll()
    }

    // button to select next time
    private val nextButton: ImageButton = createImageButton(
        "basic/white-right-arrow",
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
        game.universeClient.nextUniverseData3D()
        updateAll()
    }

    private val universeDataSelectBox: SelectBox<String> = createSelectBox(
        game.universeClient.getAvailableData3DName(),
        "",
        gdxSetting.smallFontSize
    ) { name, _ ->
        game.universeClient.pickUniverseData3D(name)
        updateAll()
    }

    private val currentUniverseDataTable: Table =  createCurrentUniverseDataTable()

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
        updateAll()
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
        updateAll()
    }

    init {
        // Set background color to blue
        table.background = assets.getBackgroundColor(0.1f, 0.1f, 0.1f, 1.0f)

        updateServerStatusLabels()

        updateCurrentUniverseDataLabel()

        table.add(currentUniverseDataTable).pad(10f)

        table.add(updateButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(updateToLatestButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(serverStatusTable).pad(10f)

    }

    override fun get(): Table {
        return table
    }

    /**
     * Only update actor in this class
     */
    override fun update() {
        updateServerStatusLabels()
        updateCurrentUniverseDataLabel()
        runBlocking {
            updateUpdateToLatestButton()
        }
        updateUniverseDataSelectionBox()
    }

    /**
     * Update this class and updatableByTopBar
     */
    fun updateAll() {
        update()
        updatableByTopBar.forEach { it() }
    }

    /**
     * Function that should be automatically call by universe client
     */
    fun autoUpdate() {
        updateServerStatusLabels()
        runBlocking {
            updateUpdateToLatestButton()
        }
    }

    /**
     * Create a table to display server status
     */
    private fun createServerStatusTable(): Table {
        val nestedTable: Table = Table()
        nestedTable.add(serverStatusNameAndTimeLabel)

        nestedTable.row()

        nestedTable.add(serverUniverseTimeLabel)

        nestedTable.row()

        nestedTable.add(timeLeftLabel)

        return nestedTable
    }

    /**
     * Create a table for controlling time slice
     */
    private fun createCurrentUniverseDataTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(currentUniverseDataLabel).colspan(3)

        nestedTable.row()

        nestedTable.add(previousButton).size(25f * gdxSetting.imageScale, 25f * gdxSetting.imageScale)

        nestedTable.add(universeDataSelectBox)

        nestedTable.add(nextButton).size(25f * gdxSetting.imageScale, 25f * gdxSetting.imageScale)

        return nestedTable
    }

    /**
     * Update the text label showing the current time slice
     */
    private fun updateCurrentUniverseDataLabel() {
        val currentUniverseName: String = game.universeClient.getUniverseData3D().universeSettings.universeName
        val currentUniverseTime: Int = game.universeClient.getUniverseData3D().center.t

        currentUniverseDataLabel.setText("Current data: $currentUniverseName - $currentUniverseTime")
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
     * Set updateToLatestButton
     */
    private suspend fun updateUpdateToLatestButton() {
        if (game.universeClient.isNewDataReady.isTrue()) {
            enableActor(updateToLatestButton)
            updateToLatestButton.setColor(1.0f, 1.0f, 1.0f, 1.0f)
        } else {
            disableActor(updateToLatestButton)
            updateToLatestButton.setColor(1.0f, 1.0f, 1.0f, 0.5f)
        }
    }

    /**
     * Update universeDataSelection Box
     */
    private fun updateUniverseDataSelectionBox() {
        universeDataSelectBox.items = Array(game.universeClient.getAvailableData3DName().toTypedArray())
    }
}