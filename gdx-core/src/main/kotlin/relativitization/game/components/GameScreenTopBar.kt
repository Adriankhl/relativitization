package relativitization.game.components

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
import relativitization.universe.data.physics.Int3D
import relativitization.universe.maths.physics.Intervals.intDelay
import kotlin.math.min


class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

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
    }

    private val universeDataSelectBox: SelectBox<String> = createSelectBox(
        game.universeClient.getAvailableData3DName(),
        "",
        gdxSetting.smallFontSize
    ) { name, _ ->
        game.universeClient.pickUniverseData3D(name)
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
        gdxSetting.soundEffectsVolume
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
        gdxSetting.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.pickLatestUniverseData3D()
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
        gdxSetting.soundEffectsVolume
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
        gdxSetting.soundEffectsVolume
    ) {
        gdxSetting.mapZoomRelativeToFullMap *= gdxSetting.mapZoomFactor
        game.changeGdxSetting()
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
        gdxSetting.soundEffectsVolume
    ) {
        gdxSetting.mapZoomRelativeToFullMap /= gdxSetting.mapZoomFactor
        game.changeGdxSetting()
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
        gdxSetting.soundEffectsVolume
    ) {
        gdxSetting.mapZoomRelativeToFullMap = 1.0f
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
        gdxSetting.soundEffectsVolume
    ) {
        runBlocking {
            val httpCode = game.universeClient.httpPostHumanInput()
            if (httpCode == HttpStatusCode.OK) {
                it.image.setColor(0.0f, 1.0f, 0.0f, 1.0f)
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
        0.8f,
        gdxSetting.soundEffectsVolume
    ) {
        gdxSetting.showingBottomCommand = it.isChecked
        game.changeGdxSetting()
    }

    private val overviewInfoButton: TextButton = createTextButton(
        "Overview",
        gdxSetting.normalFontSize,
        gdxSetting.soundEffectsVolume
    ) {
        // If hiding, show the panel
        if (!gdxSetting.showingInfo) {
            gdxSetting.showingInfo = true
            gdxSetting.showingInfoType = ShowingInfoType.OVERVIEW
        } else {
            gdxSetting.showingInfo = false
        }
        game.changeGdxSetting()
    }

    private val physicsInfoButton: TextButton = createTextButton(
        "Physics",
        gdxSetting.normalFontSize,
        gdxSetting.soundEffectsVolume
    ) {
        // If hiding, show the panel
        if (!gdxSetting.showingInfo) {
            gdxSetting.showingInfo = true
            gdxSetting.showingInfoType = ShowingInfoType.PHYSICS
        } else {
            gdxSetting.showingInfo = false
        }
        game.changeGdxSetting()
    }

    private val tCoordinateLabel = createLabel(
        "t: ${game.universeClient.getUniverseData3D().center.t}",
        gdxSetting.smallFontSize
    )

    private val xCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.xDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.x,
        gdxSetting.smallFontSize
    ) { x, _ ->
        updateCoordinates(Int3D(x, yCoordinateSelectBox.selected, zCoordinateSelectBox.selected))
    }

    private val yCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.yDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.y,
        gdxSetting.smallFontSize
    ) { y, _ ->
        updateCoordinates(Int3D(xCoordinateSelectBox.selected, y, zCoordinateSelectBox.selected))
    }

    private val zCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        (0 until game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        game.universeClient.universeClientSettings.viewCenter.z,
        gdxSetting.smallFontSize
    ) { z, _ ->
        updateCoordinates(Int3D(xCoordinateSelectBox.selected, yCoordinateSelectBox.selected, z))
    }

    private val zLimitSelectBox: SelectBox<Int> = createSelectBox(
        (1..game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        min(game.universeClient.universeClientSettings.zLimit, game.universeClient.getUniverseData3D().universeSettings.zDim)
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
        gdxSetting.soundEffectsVolume
    ) {
        game.universeClient.universeClientSettings.viewCenter.x  = xCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.y  = yCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.z  = zCoordinateSelectBox.selected
        game.universeClient.changeUniverseDataView()
    }


    private val viewControlTable: Table = createViewControlTable()

    private val currentUniverseDataTable: Table =  createCurrentUniverseDataTable()

    private val serverStatusNameAndTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val serverUniverseTimeLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val timeLeftLabel: Label = createLabel("", gdxSetting.smallFontSize)
    private val serverStatusTable: Table = createServerStatusTable()


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
        gdxSetting.soundEffectsVolume
    ) {
        game.screen = GdxSettingsScreen(game, true)
    }

    init {
        // Set background color to blue
        table.background = assets.getBackgroundColor(0.1f, 0.1f, 0.1f, 1.0f)

        updateServerStatusLabels()

        updateCurrentUniverseDataLabel()

        table.add(viewControlTable).pad(10f)

        table.add(currentUniverseDataTable).pad(10f)

        table.add(updateToLatestButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(clearSelectedButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(clearCommandListButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(uploadButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(bottomCommandInfoButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)

        table.add(overviewInfoButton).pad(10f)

        table.add(physicsInfoButton).pad(10f)

        table.add(serverStatusTable).pad(10f)

        table.add(settingButton).size(50f * gdxSetting.imageScale, 50f * gdxSetting.imageScale)
    }

    override fun getActor(): ScrollPane {
        return scrollPane
    }


    override fun onServerStatusChange() {
        updateServerStatusLabels()
        runBlocking {
            updateUpdateToLatestButton()
        }
    }

    override fun onUniverseData3DChange() {
        updateServerStatusLabels()
        updateCurrentUniverseDataLabel()
        runBlocking {
            updateUpdateToLatestButton()
        }
        updateUniverseDataSelectionBox()
    }


    override fun onUniverseDataViewChange() {
        updateCoordinates(game.universeClient.primarySelectedInt3D)
    }


    /**
     * Create table for controlling view int3D, z limit and zoom
     */
    private fun createViewControlTable(): Table {
        val nestedTable: Table = Table()

        val topTable: Table = Table()
        val bottomTable: Table = Table()

        topTable.add(tCoordinateLabel).space(10f)
        topTable.add(createLabel("x:", gdxSetting.smallFontSize))
        topTable.add(xCoordinateSelectBox).space(10f)
        topTable.add(createLabel("y:", gdxSetting.smallFontSize))
        topTable.add(yCoordinateSelectBox).space(10f)
        topTable.add(createLabel("z:", gdxSetting.smallFontSize))
        topTable.add(zCoordinateSelectBox).space(10f)

        bottomTable.add(createLabel("z limit:", gdxSetting.smallFontSize))
        bottomTable.add(zLimitSelectBox).space(10f)
        bottomTable.add(confirmViewButton).size(40f * gdxSetting.imageScale, 40f * gdxSetting.imageScale)
        bottomTable.add(zoomInButton).size(40f * gdxSetting.imageScale, 40f * gdxSetting.imageScale)
        bottomTable.add(zoomOutButton).size(40f * gdxSetting.imageScale, 40f * gdxSetting.imageScale)
        bottomTable.add(zoomToFullMapButton).size(40f * gdxSetting.imageScale, 40f * gdxSetting.imageScale)

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

        nestedTable.add(currentUniverseDataLabel).colspan(3)

        nestedTable.row().space(10f)

        nestedTable.add(previousButton).size(30f * gdxSetting.imageScale, 30f * gdxSetting.imageScale)

        nestedTable.add(universeDataSelectBox)

        nestedTable.add(nextButton).size(30f * gdxSetting.imageScale, 30f * gdxSetting.imageScale)

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
            "connected"
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
        universeDataSelectBox.items = Array(game.universeClient.getAvailableData3DName().toTypedArray())
    }

    /**
     * Update coordinates
     */
    private fun updateCoordinates(int3D: Int3D) {
        xCoordinateSelectBox.selected = int3D.x
        yCoordinateSelectBox.selected = int3D.y
        zCoordinateSelectBox.selected = int3D.z

        val t: Int = game.universeClient.getUniverseData3D().center.t - intDelay(
            int3D,
            game.universeClient.getUniverseData3D().center.toInt3D(),
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight
        )

        tCoordinateLabel.setText("t: $t")
    }
}