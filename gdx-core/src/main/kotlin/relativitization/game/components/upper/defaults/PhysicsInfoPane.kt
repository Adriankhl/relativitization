package relativitization.game.components.upper.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.universe.core.data.PlayerData
import relativitization.universe.game.data.commands.AddEventCommand
import relativitization.universe.game.data.commands.ChangeVelocityCommand
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.events.MoveToDouble3DEvent
import relativitization.universe.core.maths.number.Notation
import relativitization.universe.core.maths.physics.Double3D
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Movement.displacementToVelocity
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.core.utils.RelativitizationLogManager

class PhysicsInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Physics"

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // observable variables for command generation
    private var targetX = createDoubleTextField(
        playerData.double4D.x,
        gdxSettings.smallFontSize
    )
    private var targetY = createDoubleTextField(
        playerData.double4D.y,
        gdxSettings.smallFontSize
    )
    private var targetZ = createDoubleTextField(
        playerData.double4D.z,
        gdxSettings.smallFontSize
    )
    private var maxSpeed = createDoubleTextField(
        game.universeClient.getUniverseData3D().universeSettings.speedOfLight * 0.5,
        gdxSettings.smallFontSize,
    )
    private var targetVelocityX = createDoubleTextField(
        playerData.velocity.vx,
        gdxSettings.smallFontSize,
    )
    private var targetVelocityY = createDoubleTextField(
        playerData.velocity.vy,
        gdxSettings.smallFontSize,
    )
    private var targetVelocityZ = createDoubleTextField(
        playerData.velocity.vz,
        gdxSettings.smallFontSize,
    )

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
        val primaryPlayerData: PlayerData = game.universeClient.getValidPrimaryPlayerData()
        if (primaryPlayerData != playerData) {
            updatePlayerData()
            updateTable()
        }
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

        // Change target position
        targetX.value = targetDouble3D.x
        targetY.value = targetDouble3D.y
        targetZ.value = targetDouble3D.z

        val targetVelocity = displacementToVelocity(
            playerData.double4D.toDouble3D(),
            targetDouble3D,
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight
        ).scaleVelocity(maxSpeed.value)

        // Change target velocity
        targetVelocityX.value = targetVelocity.vx
        targetVelocityY.value = targetVelocity.vy
        targetVelocityZ.value = targetVelocity.vz
    }

    override fun onSelectedPlayerIdListChange() {
        if (game.universeClient.newSelectedPlayerId != playerData.playerId) {

            val targetDouble3D: Double3D = game.universeClient.getUniverseData3D().get(
                game.universeClient.newSelectedPlayerId
            ).groupCenterDouble3D(
                game.universeClient.getUniverseData3D().universeSettings.groupEdgeLength
            )

            // Change target position
            targetX.value = targetDouble3D.x
            targetY.value = targetDouble3D.y
            targetZ.value = targetDouble3D.z

            val targetVelocity = displacementToVelocity(
                playerData.double4D.toDouble3D(),
                targetDouble3D,
                game.universeClient.getUniverseData3D().universeSettings.speedOfLight
            ).scaleVelocity(maxSpeed.value)

            // Change target velocity
            targetVelocityX.value = targetVelocity.vx
            targetVelocityY.value = targetVelocity.vy
            targetVelocityZ.value = targetVelocity.vz
        }
    }


    private fun updatePlayerData() {
        playerData = game.universeClient.getValidPrimaryPlayerData()
    }

    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Physics: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

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
                "Movement commands:",
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

        val timeDilationCounterLabel = createLabel(
            "Time dilation counter: ${playerData.timeDilationCounter}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(timeDilationCounterLabel)

        nestedTable.row().space(10f)

        val dilationActionLabel = createLabel(
            "Dilation action: ${playerData.isTimeDilationActionTurn}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(dilationActionLabel)

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

        nestedTable.add(maxSpeed.textField)

        nestedTable.row().space(10f)

        val maxSpeedSliderContainer = createSliderContainer(
            min = 0f,
            max = game.universeClient.getUniverseData3D().universeSettings
                .speedOfLight.toFloat() - 0.01f,
            stepSize = 0.01f,
            default = maxSpeed.value.toFloat(),
            width = 150f * gdxSettings.imageScale,
            height = 15f * gdxSettings.imageScale,
        ) { fl, _ ->
            val newSpeed: Double = Notation.roundDecimal(fl.toDouble(), 2)

            maxSpeed.value = newSpeed
        }
        nestedTable.add(maxSpeedSliderContainer).colspan(2)

        return nestedTable
    }

    private fun createChangeVelocityTable(): Table {
        val nestedTable = Table()

        val changeVelocityCommandTextButton = createTextButton(
            text = "Change velocity",
            fontSize = gdxSettings.normalFontSize,
            soundVolume = gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeVelocityCommand = ChangeVelocityCommand(
                toId = playerData.playerId,
                targetVelocity = Velocity(
                    targetVelocityX.value,
                    targetVelocityY.value,
                    targetVelocityZ.value
                ),
            )

            game.universeClient.currentCommand = changeVelocityCommand
        }


        nestedTable.add(changeVelocityCommandTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Target vx: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(targetVelocityX.textField)

        nestedTable.row().space(10f)

        val targetVelocityYLabel = createLabel("Target vy: ", gdxSettings.smallFontSize)
        nestedTable.add(targetVelocityYLabel)
        nestedTable.add(targetVelocityY.textField)

        nestedTable.row().space(10f)

        val targetVelocityZLabel = createLabel("Target vz: ", gdxSettings.smallFontSize)
        nestedTable.add(targetVelocityZLabel)
        nestedTable.add(targetVelocityZ.textField).space(10f)


        return nestedTable
    }

    private fun createMoveToDouble3DTable(): Table {
        val nestedTable = Table()

        val moveToDouble3DEventCommandTextButton = createTextButton(
            text = "Move to location",
            fontSize = gdxSettings.normalFontSize,
            soundVolume = gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor
        ) {
            val moveToDouble3DEvent = MoveToDouble3DEvent(
                toId = playerData.playerId,
                targetDouble3D = Double3D(targetX.value, targetY.value, targetZ.value),
                maxSpeed = maxSpeed.value,
            )

            val addEventCommand = AddEventCommand(
                event = moveToDouble3DEvent,
            )

            game.universeClient.currentCommand = addEventCommand
        }


        nestedTable.add(moveToDouble3DEventCommandTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(createLabel("Target x: ", gdxSettings.smallFontSize))
        nestedTable.add(targetX.textField)

        nestedTable.row().space(10f)

        nestedTable.add(createLabel("Target y: ", gdxSettings.smallFontSize))
        nestedTable.add(targetY.textField)

        nestedTable.row().space(10f)

        nestedTable.add(createLabel("Target z: ", gdxSettings.smallFontSize))
        nestedTable.add(targetZ.textField)

        return nestedTable
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}