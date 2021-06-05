package relativitization.game.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.ShowingInfoType
import relativitization.game.screens.GdxSettingsScreen
import relativitization.game.utils.ScreenComponent
import relativitization.universe.maths.physics.Intervals.intDelay
import kotlin.math.min


class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings
    private val table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

    private val currentUniverseDataLabel: Label = createLabel("", gdxSettings.smallFontSize)

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
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.previousUniverseData3D()
        }
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
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.nextUniverseData3D()
        }
    }

    private val universeDataSelectBox: SelectBox<String> = createSelectBox(
        runBlocking { game.universeClient.getAvailableData3DName() },
        "",
        gdxSettings.smallFontSize
    ) { data3DName, _ ->
        runBlocking {
            // Only change this when the name is different to prevent infinite loop
            if (game.universeClient.getCurrentData3DName() != data3DName) {
                game.universeClient.pickUniverseData3D(data3DName)
            }
        }
    }

    private val clearOldDataButton: ImageButton = createImageButton(
        "basic/white-bin",
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
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.clearOldData3D()
        }
    }

    private val clearSelectedButton: ImageButton = createImageButton(
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
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.clearSelected()
    }

    private val updateToLatestButton: ImageButton = createImageButton(
        "basic/white-rightmost-triangle",
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
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.pickLatestUniverseData3D()

            // Also clear command list when updating to latest data
            game.universeClient.clearCommandList()

            updateUpdateToLatestButton()
        }
    }

    private val clearCommandListButton: ImageButton = createImageButton(
        "basic/white-stop",
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
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.clearCommandList()
    }

    private val zoomInButton: ImageButton = createImageButton(
        "basic/white-zoom-in",
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
        gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap *= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    private val zoomOutButton: ImageButton = createImageButton(
        "basic/white-zoom-out",
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
        gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap /= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    private val zoomToFullMapButton: ImageButton = createImageButton(
        "basic/white-four-out-arrow",
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
        gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap = 1.0f
        game.changeGdxSettings()
    }

    private val uploadButton: ImageButton = createImageButton(
        "basic/white-upload",
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
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            val httpCode = game.universeClient.httpPostHumanInput()
            if (httpCode == HttpStatusCode.OK) {
                it.image.setColor(0.0f, 1.0f, 0.0f, 1.0f)

                // Ask server to stop waiting
                if (stopWaitingSelectBox.selected == "After input") {
                    game.universeClient.httpPostStopWaiting()
                }
            } else {
                it.image.setColor(1.0f, 0.0f, 0.0f, 1.0f)
            }
        }
    }

    private val bottomCommandInfoButton: ImageButton = createImageButton(
        "basic/white-wait-upload",
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
        0.5f,
        gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.showingBottomCommand = !it.isChecked
        game.changeGdxSettings()
    }

    private val overviewInfoButton: TextButton = createTextButton(
        "Overview",
        gdxSettings.normalFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        // If hiding, show the panel
        if ((gdxSettings.showingInfoType == ShowingInfoType.OVERVIEW) && gdxSettings.showingInfo) {
            gdxSettings.showingInfo = false
            gdxSettings.showingInfoType = ShowingInfoType.OVERVIEW
        } else {
            gdxSettings.showingInfo = true
            gdxSettings.showingInfoType = ShowingInfoType.OVERVIEW
        }
        game.changeGdxSettings()
    }

    private val physicsInfoButton: TextButton = createTextButton(
        "Physics",
        gdxSettings.normalFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        // If hiding, show the panel
        if ((gdxSettings.showingInfoType == ShowingInfoType.PHYSICS) && gdxSettings.showingInfo) {
            gdxSettings.showingInfo = false
            gdxSettings.showingInfoType = ShowingInfoType.PHYSICS
        } else {
            gdxSettings.showingInfo = true
            gdxSettings.showingInfoType = ShowingInfoType.PHYSICS
        }
        game.changeGdxSettings()
    }

    private val tCoordinateLabel = createLabel(
        "t: ${game.universeClient.getUniverseData3D().center.t}",
        gdxSettings.smallFontSize
    )

    private val xCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.xDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.x,
        gdxSettings.smallFontSize
    ) { x, _ ->
        game.universeClient.primarySelectedInt3D = game.universeClient.primarySelectedInt3D.copy(x = x)
    }

    private val yCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.yDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.y,
        gdxSettings.smallFontSize
    ) { y, _ ->
        game.universeClient.primarySelectedInt3D = game.universeClient.primarySelectedInt3D.copy(y = y)
    }

    private val zCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.z,
        gdxSettings.smallFontSize
    ) { z, _ ->
        game.universeClient.primarySelectedInt3D = game.universeClient.primarySelectedInt3D.copy(z = z)
    }

    private val zLimitSelectBox: SelectBox<Int> = createSelectBox(
        (1..game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        min(game.universeClient.universeClientSettings.zLimit, game.universeClient.getUniverseData3D().universeSettings.zDim),
        gdxSettings.smallFontSize
    ) { zLimit, _ ->
        game.universeClient.universeClientSettings.zLimit = zLimit
    }

    private val confirmViewButton: ImageButton = createImageButton(
        "basic/white-tick",
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
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.universeClientSettings.viewCenter.x  = xCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.y  = yCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.z  = zCoordinateSelectBox.selected
        game.universeClient.changeUniverseDataView()
    }

    private val stopWaitingSelectBox: SelectBox<String> = createSelectBox(
        listOf("No", "After input", "Always"),
        "No",
        gdxSettings.smallFontSize
    )

    private val settingButton: ImageButton = createImageButton(
        "basic/white-setting",
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
        gdxSettings.soundEffectsVolume
    ) {
        game.screen = GdxSettingsScreen(game, true)
    }

    private val runUniverseButton = createTextButton(
        "Run",
        gdxSettings.smallFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.httpPostRunUniverse()
        }
    }

    private val stopUniverseButton = createTextButton(
        "Stop",
        gdxSettings.smallFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.httpPostStopUniverse()
        }
    }

    // About server status
    private val serverStatusNameAndTimeLabel: Label = createLabel("", gdxSettings.smallFontSize)
    private val serverUniverseTimeLabel: Label = createLabel("", gdxSettings.smallFontSize)
    private val timeLeftLabel: Label = createLabel("", gdxSettings.smallFontSize)

    // Tables
    private val viewControlTable: Table = createViewControlTable()
    private val currentUniverseDataTable: Table =  createCurrentUniverseDataTable()
    private val serverStatusTable: Table = createServerStatusTable()
    private val stopWaitingTable: Table = createStopWaitingTable()
    private val runOrStopUniverseTable: Table = createRunStopUniverseTable()


    init {
        // Set background color to blue
        table.background = assets.getBackgroundColor(0.1f, 0.1f, 0.1f, 1.0f)

        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(false, true)
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateServerStatusLabels()

        updateCurrentUniverseDataLabel()

        table.add(viewControlTable).pad(10f)

        table.add(currentUniverseDataTable).pad(10f)

        table.add(updateToLatestButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(clearSelectedButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(clearCommandListButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(overviewInfoButton).pad(10f)

        table.add(physicsInfoButton).pad(10f)

        table.add(bottomCommandInfoButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(serverStatusTable).pad(10f)

        table.add(stopWaitingTable).pad(10f)

        table.add(runOrStopUniverseTable).pad(10f)

        table.add(uploadButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(settingButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }


    override fun onServerStatusChange() {
        updateServerStatusLabels()
        updateRunAndStopButton()
        runBlocking {
            updateUpdateToLatestButton()

            if (game.universeClient.getCurrentServerStatus().isServerWaitingInput) {
                // Ask server to stop waiting
                if (stopWaitingSelectBox.selected == "Always") {
                    game.universeClient.httpPostStopWaiting()
                }
            }
        }
        Gdx.graphics.requestRendering()
    }

    override fun onUniverseData3DChange() {
        updateServerStatusLabels()
        updateCurrentUniverseDataLabel()
        runBlocking {
            updateUpdateToLatestButton()
        }
        updateUniverseDataSelectionBox()
    }


    override fun onPrimarySelectedInt3DChange() {
        // Update select box if different
        if (game.universeClient.primarySelectedInt3D.x != xCoordinateSelectBox.selected) {
            xCoordinateSelectBox.selected = game.universeClient.primarySelectedInt3D.x
        }
        if (game.universeClient.primarySelectedInt3D.y != yCoordinateSelectBox.selected) {
            yCoordinateSelectBox.selected = game.universeClient.primarySelectedInt3D.y
        }
        if (game.universeClient.primarySelectedInt3D.z != zCoordinateSelectBox.selected) {
            zCoordinateSelectBox.selected = game.universeClient.primarySelectedInt3D.z
        }

        updateTCoordinate()
    }

    /**
     * Create table for controlling stop waiting
     */
    private fun createStopWaitingTable(): Table {
        val nestedTable: Table = Table()
        nestedTable.add(createLabel("Server stop waiting (admin): ", gdxSettings.smallFontSize))
        nestedTable.row().space(10f)
        nestedTable.add(stopWaitingSelectBox)
        return nestedTable
    }


    /**
     * Create table for controlling view int3D, z limit and zoom
     */
    private fun createViewControlTable(): Table {
        val nestedTable: Table = Table()

        val topTable: Table = Table()
        val bottomTable: Table = Table()

        topTable.add(tCoordinateLabel).space(10f)
        topTable.add(createLabel("x:", gdxSettings.smallFontSize))
        topTable.add(xCoordinateSelectBox).space(10f)
        topTable.add(createLabel("y:", gdxSettings.smallFontSize))
        topTable.add(yCoordinateSelectBox).space(10f)
        topTable.add(createLabel("z:", gdxSettings.smallFontSize))
        topTable.add(zCoordinateSelectBox).space(10f)

        bottomTable.add(createLabel("z limit:", gdxSettings.smallFontSize))
        bottomTable.add(zLimitSelectBox).space(10f)
        bottomTable.add(confirmViewButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)
        bottomTable.add(zoomInButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)
        bottomTable.add(zoomOutButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)
        bottomTable.add(zoomToFullMapButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        nestedTable.add(topTable)

        nestedTable.row().space(10f)

        nestedTable.add(bottomTable)

        return nestedTable
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

        val topTable: Table = Table()

        val bottomTable: Table = Table()

        topTable.add(previousButton).size(30f * gdxSettings.imageScale, 30f * gdxSettings.imageScale)

        topTable.add(universeDataSelectBox)

        topTable.add(nextButton).size(30f * gdxSettings.imageScale, 30f * gdxSettings.imageScale)

        bottomTable.add(clearOldDataButton).size(30f * gdxSettings.imageScale, 30f * gdxSettings.imageScale)

        nestedTable.add(topTable)

        nestedTable.row().space(10f)

        nestedTable.add(bottomTable)

        return nestedTable
    }

    /**
     * Create a table for running or stopping universe
     */
    private fun createRunStopUniverseTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(createLabel("Run or stop server:", gdxSettings.smallFontSize)).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(runUniverseButton).space(10f)

        nestedTable.add(stopUniverseButton)

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
        val serverStatus = game.universeClient.getCurrentServerStatus().copy()

        val connectionText = if (serverStatus.success) {
            if (serverStatus.isUniverseRunning) {
                "running"
            } else {
                "stopped"
            }
        } else {
            "disconnected"
        }

        val timeLeftText = if (serverStatus.isServerWaitingInput) {
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
        runBlocking {
            universeDataSelectBox.items = Array(game.universeClient.getAvailableData3DName().toTypedArray())
            universeDataSelectBox.selected = game.universeClient.getCurrentData3DName()
        }
    }

    /**
     * Update coordinates
     */
    private fun updateTCoordinate() {
        val t: Int = game.universeClient.getUniverseData3D().center.t - intDelay(
            game.universeClient.primarySelectedInt3D,
            game.universeClient.getUniverseData3D().center.toInt3D(),
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight
        )

        tCoordinateLabel.setText("t: $t")
    }

    /**
     * Update run button and stop button
     */
    private fun updateRunAndStopButton() {
        if (game.universeClient.getCurrentServerStatus().isUniverseRunning) {
            disableActor(runUniverseButton)
            enableActor(stopUniverseButton)
        } else {
            enableActor(runUniverseButton)
            disableActor(stopUniverseButton)
        }
    }
}