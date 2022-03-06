package relativitization.game.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfo
import relativitization.game.screens.ClientSettingsScreen
import relativitization.game.screens.HelpScreen
import relativitization.game.utils.ScreenComponent
import relativitization.universe.communication.UniverseServerStatusMessage
import relativitization.universe.data.commands.DummyCommand
import relativitization.universe.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.data.components.physicsData
import relativitization.universe.maths.number.toScientificNotation
import relativitization.universe.maths.physics.Intervals.intDelay
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.full.primaryConstructor


class GameScreenTopBar(
    val game: RelativitizationGame,
    private val syncGameScreenComponentSettings: () -> Unit,
) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings
    private val table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

    private val currentUniverseDataLabel: Label = createLabel("", gdxSettings.smallFontSize)

    // button to select previous time
    private val previousUniverseData3DButton: ImageButton = createImageButton(
        name = "basic/white-left-arrow",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.previousUniverseData3D()
            updatePreviousNextUniverseDataButton()
        }
    }

    // button to select next time
    private val nextUniverseData3DButton: ImageButton = createImageButton(
        name = "basic/white-right-arrow",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.nextUniverseData3D()
            updatePreviousNextUniverseDataButton()
        }
    }

    private val universeDataSelectBoxContainer = Container(
        createSelectBox(
            itemList = runBlocking { game.universeClient.getAvailableData3DName() },
            default = runBlocking { game.universeClient.getCurrentData3DName() },
            fontSize = gdxSettings.smallFontSize
        ) { data3DName, _ ->
            runBlocking {
                // Only change this when the name is different to prevent infinite loop
                if (game.universeClient.getCurrentData3DName() != data3DName) {
                    game.universeClient.pickUniverseData3D(data3DName)
                }
            }
        }
    )

    //private val clearOldDataButton: ImageButton = createImageButton(
    //    name = "basic/white-bin",
    //    rUp = 1.0f,
    //    gUp = 1.0f,
    //    bUp = 1.0f,
    //    aUp = 1.0f,
    //    rDown = 1.0f,
    //    gDown = 1.0f,
    //    bDown = 1.0f,
    //    aDown = 0.7f,
    //    rChecked = 1.0f,
    //    gChecked = 1.0f,
    //    bChecked = 1.0f,
    //    aChecked = 1.0f,
    //    soundVolume = gdxSettings.soundEffectsVolume
    //) {
    //    runBlocking {
    //        game.universeClient.clearOldData3D()
    //    }
    //}

    private val clearSelectedButton: ImageButton = createImageButton(
        name = "basic/white-circle-arrow",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.clearSelected()
    }

    private val updateToLatestButton: ImageButton = createImageButton(
        name = "basic/white-rightmost-triangle",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.pickLatestUniverseData3D()

            updateUpdateToLatestButton()

            updatePreviousNextUniverseDataButton()

            game.universeClient.currentCommand = DummyCommand()
        }

        uploadButton.image.setColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private val clearCommandListButton: ImageButton = createImageButton(
        name = "basic/white-stop",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.clearCommandList()
    }

    private val zoomInButton: ImageButton = createImageButton(
        name = "basic/white-zoom-in",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap *= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    private val zoomOutButton: ImageButton = createImageButton(
        name = "basic/white-zoom-out",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap /= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    private val zoomToFullMapButton: ImageButton = createImageButton(
        name = "basic/white-four-out-arrow",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.mapZoomRelativeToFullMap = 1.0f
        game.changeGdxSettings()
    }

    private val uploadButton: ImageButton = createImageButton(
        name = "basic/white-upload",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            val httpCode = game.universeClient.httpPostHumanInput()
            if (httpCode == HttpStatusCode.OK) {
                it.image.setColor(0.0f, 1.0f, 0.0f, 1.0f)

                // Ask server to stop waiting
                if (waitSelectBox.selected == "You only") {
                    game.universeClient.httpPostStopWaiting()
                }
            } else {
                it.image.setColor(1.0f, 0.0f, 0.0f, 1.0f)
            }
        }
    }

    private val bottomCommandInfoButton: ImageButton = createImageButton(
        name = "basic/white-wait-upload",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 0.5f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.isBottomCommandInfoShowing = !it.isChecked
        syncGameScreenComponentSettings()
        game.changeGdxSettings()
    }

    private val upperInfoButtonList: List<TextButton> = UpperInfo::class.sealedSubclasses.map {
        it.primaryConstructor!!.call(game)
    }.sortedBy {
        it.infoPriority
    }.map {
        it.infoName
    }.map { infoName ->
        createTextButton(
            infoName,
            fontSize = gdxSettings.normalFontSize,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            // If hiding, show the panel
            if ((gdxSettings.showingUpperInfo == infoName) && gdxSettings.isInfoShowing) {
                gdxSettings.isInfoShowing = false
                gdxSettings.showingUpperInfo = infoName
            } else {
                gdxSettings.isInfoShowing = true
                gdxSettings.showingUpperInfo = infoName
            }

            syncGameScreenComponentSettings()
            game.changeGdxSettings()
        }
    }

    private val tCoordinateLabel = createLabel(
        text = "t: ${game.universeClient.getUniverseData3D().center.t}",
        fontSize = gdxSettings.smallFontSize
    )

    private val xCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        itemList = (0 until game.universeClient.getUniverseData3D().universeSettings.xDim).toList(),
        default = game.universeClient.universeClientSettings.viewCenter.x,
        fontSize = gdxSettings.smallFontSize
    ) { x, _ ->
        game.universeClient.primarySelectedInt3D =
            game.universeClient.primarySelectedInt3D.copy(x = x)
    }

    private val yCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        itemList = (0 until game.universeClient.getUniverseData3D().universeSettings.yDim).toList(),
        default = game.universeClient.universeClientSettings.viewCenter.y,
        fontSize = gdxSettings.smallFontSize
    ) { y, _ ->
        game.universeClient.primarySelectedInt3D =
            game.universeClient.primarySelectedInt3D.copy(y = y)
    }

    private val zCoordinateSelectBox: SelectBox<Int> = createSelectBox(
        itemList = (0 until game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        default = game.universeClient.universeClientSettings.viewCenter.z,
        fontSize = gdxSettings.smallFontSize
    ) { z, _ ->
        game.universeClient.primarySelectedInt3D =
            game.universeClient.primarySelectedInt3D.copy(z = z)
    }

    private val zLimitSelectBox: SelectBox<Int> = createSelectBox(
        itemList = (1..game.universeClient.getUniverseData3D().universeSettings.zDim).toList(),
        default = min(
            game.universeClient.universeClientSettings.zLimit,
            game.universeClient.getUniverseData3D().universeSettings.zDim
        ),
        fontSize = gdxSettings.smallFontSize
    ) { zLimit, _ ->
        game.universeClient.universeClientSettings.zLimit = zLimit
    }

    private val confirmViewButton: ImageButton = createImageButton(
        name = "basic/white-tick",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.universeClientSettings.viewCenter.x = xCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.y = yCoordinateSelectBox.selected
        game.universeClient.universeClientSettings.viewCenter.z = zCoordinateSelectBox.selected
        game.universeClient.changeUniverseDataView()
    }

    private val waitSelectBox: SelectBox<String> = createSelectBox(
        itemList = listOf("All human", "You only", "No"),
        default = "All human",
        fontSize = gdxSettings.smallFontSize
    )

    private val settingButton: ImageButton = createImageButton(
        name = "basic/white-setting",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.screen = ClientSettingsScreen(game, true)
    }

    private val runUniverseButton = createTextButton(
        text = "Run",
        fontSize = gdxSettings.smallFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.httpPostRunUniverse()
        }
    }

    private val stopUniverseButton = createTextButton(
        text = "Stop",
        fontSize = gdxSettings.smallFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        runBlocking {
            game.universeClient.httpPostStopUniverse()
        }
    }

    private val planButton: ImageButton = createImageButton(
        name = "basic/white-pen",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 0.5f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.showMutablePlayerDataFromPlan = !it.isChecked
    }

    private val helpButton: ImageButton = createImageButton(
        name = "basic/white-question-mark",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.screen = HelpScreen(game, true)
    }

    // About server status
    private var oldServerStatus: UniverseServerStatusMessage =
        game.universeClient.getCurrentServerStatus()
    private val serverStatusLabel: Label = createLabel("", gdxSettings.smallFontSize)

    // About fuel
    private val fuelLabel: Label = createLabel("", gdxSettings.smallFontSize)

    // Tables
    private val viewControlTable: Table = createViewControlTable()
    private val currentUniverseDataTable: Table = createCurrentUniverseDataTable()
    private val stopWaitingTable: Table = createStopWaitingTable()
    private val runOrStopUniverseTable: Table = createRunStopUniverseTable()

    init {
        // Set background color to blue
        table.background = assets.getBackgroundColor(0.1f, 0.1f, 0.1f, 1.0f)

        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(false, true)
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateServerStatusLabel()

        updateCurrentUniverseDataLabel()

        updateFuelRestMassDataLabel()

        table.add(viewControlTable).pad(10f)

        table.add(currentUniverseDataTable).pad(10f)

        table.add(planButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(clearSelectedButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(clearCommandListButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(fuelLabel).pad(10f)

        upperInfoButtonList.forEach {
            table.add(it).pad(10f)
        }

        table.add(bottomCommandInfoButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(serverStatusLabel).pad(10f)

        table.add(updateToLatestButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(stopWaitingTable).pad(10f)

        table.add(runOrStopUniverseTable).pad(10f)

        table.add(uploadButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(settingButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        table.add(helpButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    /**
     * Put these functions here to ensure thread safe
     */
    fun render() {
        updateServerStatusLabel()
        updateRunAndStopButton()
        runBlocking {
            updatePreviousNextUniverseDataButton()
            updateUpdateToLatestButton()
        }
    }

    override fun onServerStatusChange() {
        runBlocking {
            if (game.universeClient.getCurrentServerStatus().isServerWaitingInput) {
                // Ask server to stop waiting
                if (waitSelectBox.selected == "No") {
                    game.universeClient.httpPostStopWaiting()
                }
            }
        }
        Gdx.graphics.requestRendering()
    }

    override fun onUniverseData3DChange() {
        updateServerStatusLabel()
        updateCurrentUniverseDataLabel()
        runBlocking {
            updateUpdateToLatestButton()
        }
        updateUniverseDataSelectionBox()
        updateFuelRestMassDataLabel()
    }

    override fun onCommandListChange() {
        updateFuelRestMassDataLabel()
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
        val nestedTable = Table()
        nestedTable.add(createLabel("Wait (admin): ", gdxSettings.smallFontSize))
        nestedTable.row().space(10f)
        nestedTable.add(waitSelectBox)
        return nestedTable
    }


    /**
     * Create table for controlling view int3D, z limit and zoom
     */
    private fun createViewControlTable(): Table {
        val nestedTable = Table()

        val leftTopTable = Table()
        val leftBottomTable = Table()
        val leftTable = Table()

        val rightTable = Table()

        leftTopTable.add(tCoordinateLabel).space(10f)

        leftTopTable.add(createLabel("z limit:", gdxSettings.smallFontSize))
        leftTopTable.add(zLimitSelectBox).space(10f)

        leftBottomTable.add(createLabel("x:", gdxSettings.smallFontSize))
        leftBottomTable.add(xCoordinateSelectBox).space(10f)

        leftBottomTable.add(createLabel("y:", gdxSettings.smallFontSize))
        leftBottomTable.add(yCoordinateSelectBox).space(10f)

        leftBottomTable.add(createLabel("z:", gdxSettings.smallFontSize))
        leftBottomTable.add(zCoordinateSelectBox).space(10f)

        rightTable.add(confirmViewButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        rightTable.add(zoomInButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        rightTable.add(zoomOutButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        rightTable.add(zoomToFullMapButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)


        leftTable.add(leftTopTable)

        leftTable.row().space(10f)

        leftTable.add(leftBottomTable)

        nestedTable.add(leftTable)

        nestedTable.add(rightTable)

        return nestedTable
    }

    /**
     * Create a table for controlling time slice
     */
    private fun createCurrentUniverseDataTable(): Table {
        val nestedTable = Table()

        nestedTable.add(universeDataSelectBoxContainer).colspan(2)

        nestedTable.row().space(5f)

        nestedTable.add(previousUniverseData3DButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        nestedTable.add(nextUniverseData3DButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        return nestedTable
    }

    /**
     * Create a table for running or stopping universe
     */
    private fun createRunStopUniverseTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Run / stop (admin):",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(runUniverseButton).space(10f)

        nestedTable.add(stopUniverseButton)

        return nestedTable
    }

    /**
     * Update the text label showing the current time slice
     */
    private fun updateCurrentUniverseDataLabel() {
        val currentUniverseName: String =
            game.universeClient.getUniverseData3D().universeSettings.universeName
        val currentUniverseTime: Int = game.universeClient.getUniverseData3D().center.t

        currentUniverseDataLabel.setText("Current data: $currentUniverseName - $currentUniverseTime")
    }

    /**
     * Update the label showing the fuel rest mass
     */
    private fun updateFuelRestMassDataLabel() {
        val fuelRestMassData: FuelRestMassData = game.universeClient.getCurrentPlayerData()
            .playerInternalData.physicsData().fuelRestMassData

        val trade: String = fuelRestMassData.trade.toScientificNotation().toString(2)

        val production: String = fuelRestMassData.production.toScientificNotation().toString(2)

        val movement: String = fuelRestMassData.movement.toScientificNotation().toString(2)

        val storage: String = fuelRestMassData.storage.toScientificNotation().toString(2)

        //val firstLine = "Fuel"
        //val secondLine = if (trade.length >= movement.length) {
        //    "$trade | $production"
        //} else {
        //    " ".repeat(movement.length - trade.length) + "$trade | $production"
        //}
        //val thirdLine = if (trade.length <= movement.length) {
        //    "$movement | $storage"
        //} else {
        //    " ".repeat(trade.length - movement.length) + "$movement | $storage"
        //}

        //val maxLength: Int = max(max(firstLine.length, secondLine.length), thirdLine.length)
        //val firstIndentNum: Int = (maxLength - firstLine.length) / 2 + 3

        //fuelLabel.setText(
        //    " ".repeat(firstIndentNum) + firstLine + "\n" +
        //            secondLine + "\n" +
        //            thirdLine
        //)

        val firstLine = "$trade | $production"
        val secondLine = "$movement | $storage"
        fuelLabel.setText(firstLine + "\n" + secondLine)
    }

    /**
     * Update the text label showing the server status
     */
    private fun updateServerStatusLabel() {
        // copy to prevent change
        val serverStatus = game.universeClient.getCurrentServerStatus()

        if (serverStatus != oldServerStatus) {
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

            val firstLine = "Status: ${serverStatus.universeName} ($connectionText)"
            val secondLine = "Server universe time: ${serverStatus.currentUniverseTime}"
            val thirdLine = "Input time left: $timeLeftText"

            val maxLength: Int = max(max(firstLine.length, secondLine.length), thirdLine.length)
            val firstIndentNum: Int = (maxLength - firstLine.length) / 2
            val secondIndentNum: Int = (maxLength - secondLine.length) / 2
            val thirdIndentNum: Int = (maxLength - thirdLine.length) / 2

            serverStatusLabel.setText(
                " ".repeat(firstIndentNum) + firstLine + " ".repeat(firstIndentNum) + "\n" +
                        " ".repeat(secondIndentNum) + secondLine + " ".repeat(secondIndentNum) + "\n" +
                        " ".repeat(thirdIndentNum) + thirdLine + " ".repeat(thirdIndentNum)
            )
        }
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
     * Update next and previous universe data button
     */
    private suspend fun updatePreviousNextUniverseDataButton() {
        if (game.universeClient.getPreviousUniverseData3D() == game.universeClient.getUniverseData3D()) {
            disableActor(previousUniverseData3DButton)
            previousUniverseData3DButton.setColor(1.0f, 1.0f, 1.0f, 0.5f)
        } else {
            enableActor(previousUniverseData3DButton)
            previousUniverseData3DButton.setColor(1.0f, 1.0f, 1.0f, 1.0f)
        }

        if (game.universeClient.getNextUniverseData3D() == game.universeClient.getUniverseData3D()) {
            disableActor(nextUniverseData3DButton)
            nextUniverseData3DButton.setColor(1.0f, 1.0f, 1.0f, 0.5f)
        } else {
            enableActor(nextUniverseData3DButton)
            nextUniverseData3DButton.setColor(1.0f, 1.0f, 1.0f, 1.0f)
        }

    }

    /**
     * Update universeDataSelection Box
     */
    private fun updateUniverseDataSelectionBox() {
        runBlocking {
            universeDataSelectBoxContainer.actor = createSelectBox(
                itemList = runBlocking { game.universeClient.getAvailableData3DName() },
                default = runBlocking { game.universeClient.getCurrentData3DName() },
                fontSize = gdxSettings.smallFontSize
            ) { data3DName, _ ->
                runBlocking {
                    // Only change this when the name is different to prevent infinite loop
                    if (game.universeClient.getCurrentData3DName() != data3DName) {
                        game.universeClient.pickUniverseData3D(data3DName)
                    }
                }
            }
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