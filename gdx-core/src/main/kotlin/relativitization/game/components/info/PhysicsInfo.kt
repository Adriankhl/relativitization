package relativitization.game.components.info

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.component.physics.Double3D
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.Velocity
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.physics.Movement.displacementToVelocity
import relativitization.universe.utils.RelativitizationLogManager

class PhysicsInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    private val targetVelocityXTextField = createTextField(
        default = "${playerData.velocity.vx}",
        fontSize = gdxSettings.smallFontSize
    )

    private val targetVelocityYTextField = createTextField(
        default = "${playerData.velocity.vy}",
        fontSize = gdxSettings.smallFontSize
    )

    private val targetVelocityZTextField = createTextField(
        default = "${playerData.velocity.vz}",
        fontSize = gdxSettings.smallFontSize
    )

    private val changeVelocityCommandTextButton = createTextButton(
        text = "Change Velocity Command",
        fontSize = gdxSettings.normalFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        try {
            val vx = targetVelocityXTextField.text.toDouble()
            val vy = targetVelocityYTextField.text.toDouble()
            val vz = targetVelocityZTextField.text.toDouble()

            val changeVelocityCommand: ChangeVelocityCommand = ChangeVelocityCommand(
                targetVelocity = Velocity(vx, vy, vz),
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
            )

            val canSend: Boolean = changeVelocityCommand.canSendFromPlayer(
                game.universeClient.planDataAtPlayer.thisPlayerData,
                game.universeClient.getUniverseData3D().universeSettings
            )

            if (canSend) {
                game.universeClient.currentCommand = changeVelocityCommand
            } else {
                game.universeClient.currentCommand = CannotSendCommand()
            }
        } catch (e: NumberFormatException) {
            logger.error("Invalid target velocity")
        }
    }


    private val targetXTextField = createTextField(
        default = "${playerData.double4D.x}",
        fontSize = gdxSettings.smallFontSize
    )

    private val targetYTextField = createTextField(
        default = "${playerData.double4D.y}",
        fontSize = gdxSettings.smallFontSize
    )

    private val targetZTextField = createTextField(
        default = "${playerData.double4D.z}",
        fontSize = gdxSettings.smallFontSize
    )

    private val maxSpeedTextField = createTextField(
        default = "0.2",
        fontSize = gdxSettings.smallFontSize
    )

    private val moveToDouble3DEventCommandTextButton = createTextButton(
        text = "Move to location",
        fontSize = gdxSettings.normalFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        try {
            val x = targetXTextField.text.toDouble()
            val y = targetYTextField.text.toDouble()
            val z = targetZTextField.text.toDouble()
            val maxSpeed = maxSpeedTextField.text.toDouble()

            val moveToDouble3DEvent = MoveToDouble3DEvent(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                targetDouble3D = Double3D(x, y, z),
                maxSpeed = maxSpeed,
                stayTime = Int.MAX_VALUE,
            )

            val addEventCommand = AddEventCommand(
                event = moveToDouble3DEvent,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
            )

            val canSend: Boolean = addEventCommand.canSendFromPlayer(
                game.universeClient.planDataAtPlayer.thisPlayerData,
                game.universeClient.getUniverseData3D().universeSettings
            )

            if (canSend) {
                game.universeClient.currentCommand = addEventCommand
            } else {
                game.universeClient.currentCommand = CannotSendCommand()
            }
        } catch (e: NumberFormatException) {
            logger.error("Invalid target velocity")
        }
    }

    init {

        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)


        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)


        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onUniverseData3DChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onPrimarySelectedInt3DChange() {

        val targetInt3D: Int3D = game.universeClient.primarySelectedInt3D
        val targetDouble3D: Double3D = targetInt3D.toDouble3DCenter()

        val targetVelocity = displacementToVelocity(
            playerData.double4D.toDouble3D(),
            targetDouble3D,
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight
        )

        // Change target velocity text
        targetVelocityXTextField.text = targetVelocity.vx.toString()
        targetVelocityYTextField.text = targetVelocity.vy.toString()
        targetVelocityZTextField.text = targetVelocity.vz.toString()

        // Change target position text
        targetXTextField.text = targetDouble3D.x.toString()
        targetYTextField.text = targetDouble3D.y.toString()
        targetZTextField.text = targetDouble3D.z.toString()
    }

    override fun onSelectedPlayerIdListChange() {
        if (game.universeClient.newSelectedPlayerId != playerData.playerId) {

            val targetDouble3D: Double3D = game.universeClient.getUniverseData3D().get(
                game.universeClient.newSelectedPlayerId
            ).groupCenterDouble3D(
                game.universeClient.getUniverseData3D().universeSettings.groupEdgeLength
            )

            val targetVelocity = displacementToVelocity(
                playerData.double4D.toDouble3D(),
                targetDouble3D,
                game.universeClient.getUniverseData3D().universeSettings.speedOfLight
            )

            // Change target velocity text
            targetVelocityXTextField.text = targetVelocity.vx.toString()
            targetVelocityYTextField.text = targetVelocity.vy.toString()
            targetVelocityZTextField.text = targetVelocity.vz.toString()

            // Change target position text
            targetXTextField.text = targetDouble3D.x.toString()
            targetYTextField.text = targetDouble3D.y.toString()
            targetZTextField.text = targetDouble3D.z.toString()
        }
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

        val headerLabel = createLabel("Physics: player ${playerData.playerId}", gdxSettings.bigFontSize)

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        val massLabel = createLabel(
            "Core rest mass: ${playerData.playerInternalData.physicsData().coreRestMass}",
            gdxSettings.smallFontSize
        )

        table.add(massLabel)

        table.row().space(10f)

        val energyLabel = createLabel(
            "Fuel rest mass: ${playerData.playerInternalData.physicsData().fuelRestMass.toString()}",
            gdxSettings.smallFontSize
        )

        table.add(energyLabel)

        table.row().space(10f)

        val moveMaxPowerLabel = createLabel(
            "Max. fuel rest mass change: ${playerData.playerInternalData.physicsData().maxDeltaFuelRestMass.toString()}",
            gdxSettings.smallFontSize
        )

        table.add(moveMaxPowerLabel)

        table.row().space(10f)

        table.add(createDouble4DTable())

        table.row().space(10f)

        table.add(createVelocityTable())

        table.row().space(20f)

        table.add(createChangeVelocityTable())

        table.row().space(20f)

        table.add(createMoveToDouble3DTable())

        table.row().space(10f)

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())
    }

    private fun createDouble4DTable(): Table {
        val nestedTable: Table = Table()

        val double4DHeaderLabel = createLabel("Coordinates: ", gdxSettings.smallFontSize)
        nestedTable.add(double4DHeaderLabel)

        nestedTable.row().space(10f)

        val double4DTLabel = createLabel(
            "t: ${playerData.double4D.t.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DTLabel)

        nestedTable.row().space(10f)

        val double4DXLabel = createLabel(
            "x: ${playerData.double4D.x.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DXLabel)

        nestedTable.row().space(10f)

        val double4DYLabel = createLabel(
            "y: ${playerData.double4D.y.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DYLabel)

        nestedTable.row().space(10f)

        val double4DZLabel = createLabel(
            "z: ${playerData.double4D.z.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DZLabel)

        return nestedTable
    }

    private fun createVelocityTable(): Table {
        val nestedTable: Table = Table()

        val velocityHeaderLabel = createLabel("Velocity: ", gdxSettings.smallFontSize)
        nestedTable.add(velocityHeaderLabel)

        nestedTable.row().space(10f)

        val velocityXLabel = createLabel(
            "vx: ${playerData.velocity.vx.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityXLabel)

        nestedTable.row().space(10f)

        val velocityYLabel = createLabel(
            "vy: ${playerData.velocity.vy.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityYLabel)

        nestedTable.row().space(10f)

        val velocityZLabel = createLabel(
            "vz: ${playerData.velocity.vz.toString()}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityZLabel)

        return nestedTable
    }

    private fun createChangeVelocityTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(changeVelocityCommandTextButton).colspan(2)

        nestedTable.row().space(10f)

        val targetVelocityXLabel = createLabel("Target vx: ", gdxSettings.smallFontSize)
        nestedTable.add(targetVelocityXLabel)
        nestedTable.add(targetVelocityXTextField)

        nestedTable.row().space(10f)

        val targetVelocityYLabel = createLabel("Target vy: ", gdxSettings.smallFontSize)
        nestedTable.add(targetVelocityYLabel)
        nestedTable.add(targetVelocityYTextField)

        nestedTable.row().space(10f)

        val targetVelocityZLabel = createLabel("Target vz: ", gdxSettings.smallFontSize)
        nestedTable.add(targetVelocityZLabel)
        nestedTable.add(targetVelocityZTextField).space(10f)


        return nestedTable
    }

    private fun createMoveToDouble3DTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(moveToDouble3DEventCommandTextButton).colspan(2)

        nestedTable.row().space(10f)

        val targetXLabel = createLabel("Target x: ", gdxSettings.smallFontSize)
        nestedTable.add(targetXLabel)
        nestedTable.add(targetXTextField)

        nestedTable.row().space(10f)

        val targetYLabel = createLabel("Target y: ", gdxSettings.smallFontSize)
        nestedTable.add(targetYLabel)
        nestedTable.add(targetYTextField)

        nestedTable.row().space(10f)

        val targetZLabel = createLabel("Target z: ", gdxSettings.smallFontSize)
        nestedTable.add(targetZLabel)
        nestedTable.add(targetZTextField)

        nestedTable.row().space(10f)

        val maxSpeedLabel = createLabel("Max. speed: ", gdxSettings.smallFontSize)
        nestedTable.add(maxSpeedLabel)
        nestedTable.add(maxSpeedTextField)

        return nestedTable
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}