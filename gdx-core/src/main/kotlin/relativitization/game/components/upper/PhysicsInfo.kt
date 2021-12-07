package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.components.defaults.physics.Double3D
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Velocity
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.maths.number.Round
import relativitization.universe.maths.physics.Movement.displacementToVelocity
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.abs

class PhysicsInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // variables for command generation
    private var maxSpeed: Double =
        game.universeClient.getUniverseData3D().universeSettings.speedOfLight
    private var targetX: Double = playerData.double4D.x
    private var targetY: Double = playerData.double4D.y
    private var targetZ: Double = playerData.double4D.z
    private var targetVelocityX: Double = playerData.velocity.vx
    private var targetVelocityY: Double = playerData.velocity.vy
    private var targetVelocityZ: Double = playerData.velocity.vz



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
                stayTime = Int.MAX_VALUE,
                targetDouble3D = Double3D(x, y, z),
                maxSpeed = maxSpeed,
            )

            val addEventCommand = AddEventCommand(
                event = moveToDouble3DEvent,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = addEventCommand
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

    override fun onCommandListChange() {
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
        ).scaleVelocity(maxSpeed)

        // Change target velocity
        targetVelocityX = targetVelocity.vx
        targetVelocityY = targetVelocity.vy
        targetVelocityZ = targetVelocity.vz

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
            ).scaleVelocity(maxSpeed)

            // Change target velocity
            targetVelocityX = targetVelocity.vx
            targetVelocityY = targetVelocity.vy
            targetVelocityZ = targetVelocity.vz

            // Change target position text
            targetXTextField.text = targetDouble3D.x.toString()
            targetYTextField.text = targetDouble3D.y.toString()
            targetZTextField.text = targetDouble3D.z.toString()
        }
    }


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Physics: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(20f)

        table.add(
            createLabel(
                "Core rest mass: ${playerData.playerInternalData.physicsData().coreRestMass}",
                gdxSettings.smallFontSize
            )
        )

        table.row().space(10f)

        table.add(
            createLabel(
                "Total fuel rest mass: ${playerData.playerInternalData.physicsData().fuelRestMassData.total()}",
                gdxSettings.smallFontSize
            )
        )

        table.row().space(10f)

        table.add(
            createLabel(
                "Max. movement fuel delta: ${playerData.playerInternalData.physicsData().fuelRestMassData.maxMovementDelta}",
                gdxSettings.smallFontSize
            )
        )

        table.row().space(10f)

        table.add(createDouble4DTable())

        table.row().space(10f)

        table.add(createVelocityTable())

        table.row().space(20f)

        table.add(createTimeDilationTable())

        table.row().space(30f)

        table.add(
            createLabel(
                "Movement Commands:",
                gdxSettings.normalFontSize
            )
        )

        table.row().space(20f)

        table.add(createMaxSpeedTable())

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
        val nestedTable = Table()

        val double4DHeaderLabel = createLabel("Coordinates: ", gdxSettings.smallFontSize)
        nestedTable.add(double4DHeaderLabel)

        nestedTable.row().space(10f)

        val double4DTLabel = createLabel(
            "t: ${playerData.double4D.t}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DTLabel)

        nestedTable.row().space(10f)

        val double4DXLabel = createLabel(
            "x: ${playerData.double4D.x}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DXLabel)

        nestedTable.row().space(10f)

        val double4DYLabel = createLabel(
            "y: ${playerData.double4D.y}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DYLabel)

        nestedTable.row().space(10f)

        val double4DZLabel = createLabel(
            "z: ${playerData.double4D.z}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(double4DZLabel)

        return nestedTable
    }

    private fun createVelocityTable(): Table {
        val nestedTable = Table()

        val velocityHeaderLabel = createLabel("Velocity: ", gdxSettings.smallFontSize)
        nestedTable.add(velocityHeaderLabel)

        nestedTable.row().space(10f)

        val velocityXLabel = createLabel(
            "vx: ${playerData.velocity.vx}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityXLabel)

        nestedTable.row().space(10f)

        val velocityYLabel = createLabel(
            "vy: ${playerData.velocity.vy}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityYLabel)

        nestedTable.row().space(10f)

        val velocityZLabel = createLabel(
            "vz: ${playerData.velocity.vz}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(velocityZLabel)

        return nestedTable
    }

    private fun createTimeDilationTable(): Table {
        val nestedTable = Table()

        val gamma: Double = Relativistic.gamma(
            playerData.velocity,
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight,
        )

        val gammaLabel = createLabel("Gamma: $gamma", gdxSettings.smallFontSize)
        nestedTable.add(gammaLabel)

        nestedTable.row().space(10f)

        val gammaInverseLabel = createLabel(
            "Gamma inverse: ${1.0 / gamma}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(gammaInverseLabel)

        nestedTable.row().space(10f)

        val dilatedTimeResidueLabel = createLabel(
            "dilated time residue: ${playerData.dilatedTimeResidue}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(dilatedTimeResidueLabel)

        nestedTable.row().space(10f)

        val newDilatedTimeResidueLabel = createLabel(
            "New dilated time residue: ${playerData.dilatedTimeResidue + 1.0 / gamma}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(newDilatedTimeResidueLabel)

        return nestedTable
    }

    private fun createMaxSpeedTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Max speed: ",
                gdxSettings.smallFontSize,
            )
        )

        val onMaxSpeedTextFieldChange: MutableList<() -> Unit> = mutableListOf()
        val maxSpeedTextField = createTextField(
            default = "$maxSpeed",
            fontSize = gdxSettings.smallFontSize
        ) { s, _ ->
            maxSpeed = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid max speed")
                maxSpeed
            }

            onMaxSpeedTextFieldChange.forEach { it() }
        }
        nestedTable.add(maxSpeedTextField)

        nestedTable.row().space(10f)

        val maxSpeedSlider = createSlider(
            min = 0f,
            max = game.universeClient.getUniverseData3D().universeSettings.speedOfLight.toFloat(),
            stepSize = 0.01f,
            default = maxSpeed.toFloat(),
        ) { fl, _ ->
            val speedOfLight: Double =
                game.universeClient.getUniverseData3D().universeSettings.speedOfLight

            val newSpeed: Double = Round.roundDecimal(fl.toDouble(), 2)

            if (abs(maxSpeed - newSpeed) > 0.0001 * speedOfLight) {
                maxSpeed = newSpeed
                maxSpeedTextField.text = maxSpeed.toString()
            }
        }
        onMaxSpeedTextFieldChange.add {
            maxSpeedSlider.value = maxSpeed.toFloat()
        }
        nestedTable.add(maxSpeedSlider).colspan(2)

        return nestedTable
    }

    private fun createChangeVelocityTable(): Table {
        val nestedTable = Table()

        val changeVelocityCommandTextButton = createTextButton(
            text = "Change Velocity",
            fontSize = gdxSettings.normalFontSize,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            val changeVelocityCommand = ChangeVelocityCommand(
                targetVelocity = Velocity(targetVelocityX, targetVelocityY, targetVelocityZ),
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = changeVelocityCommand
        }

        val onTargetVelocityXTextFieldChange: MutableList<() -> Unit> = mutableListOf()
        val targetVelocityXTextField = createTextField(
            default = "$targetVelocityX",
            fontSize = gdxSettings.smallFontSize
        ) { s, _ ->
            targetVelocityX = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid target velocity X")
                targetVelocityX
            }

            onTargetVelocityXTextFieldChange.forEach { it() }
        }

        val onTargetVelocityYTextFieldChange: MutableList<() -> Unit> = mutableListOf()
        val targetVelocityYTextField = createTextField(
            default = "$targetVelocityY",
            fontSize = gdxSettings.smallFontSize
        ) { s, _ ->
            targetVelocityY = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid target velocity Y")
                targetVelocityY
            }

            onTargetVelocityYTextFieldChange.forEach { it() }
        }

        val onTargetVelocityZTextFieldChange: MutableList<() -> Unit> = mutableListOf()
        val targetVelocityZTextField = createTextField(
            default = "$targetVelocityZ",
            fontSize = gdxSettings.smallFontSize
        ) { s, _ ->
            targetVelocityZ = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid target velocity Z")
                targetVelocityZ
            }

            onTargetVelocityZTextFieldChange.forEach { it() }
        }

        nestedTable.add(changeVelocityCommandTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(createLabel(
            "Target vx: ",
            gdxSettings.smallFontSize
        ))

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
        val nestedTable = Table()

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