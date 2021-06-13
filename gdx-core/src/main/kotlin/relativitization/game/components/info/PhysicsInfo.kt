package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Movement.displacementToVelocity
import relativitization.universe.utils.RelativitizationLogManager

class PhysicsInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private var playerData: PlayerData = PlayerData(-1)

    private val changeVelocityCommandTextButton = createTextButton(
        "Change Velocity Command",
        gdxSettings.normalFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        try {
            val vx = targetVelocityXTextField.text.toDouble()
            val vy = targetVelocityYTextField.text.toDouble()
            val vz = targetVelocityZTextField.text.toDouble()

            val changeVelocityCommand: ChangeVelocityCommand = ChangeVelocityCommand(
                targetVelocity = Velocity(vx, vy, vz),
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().id,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                toId = playerData.id,
            )

            val canSend: Boolean = changeVelocityCommand.canSendFromPlayer(
                game.universeClient.getUniverseData3D().getCurrentPlayerData(),
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

    private val targetVelocityXTextField = createTextField(
        "${playerData.velocity.vx}".take(6),
        gdxSettings.smallFontSize
    )

    private val targetVelocityYTextField = createTextField(
        "${playerData.velocity.vy}".take(6),
        gdxSettings.smallFontSize
    )

    private val targetVelocityZTextField = createTextField(
        "${playerData.velocity.vz}".take(6),
        gdxSettings.smallFontSize
    )

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)


        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
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

        val targetVelocity = displacementToVelocity(
            playerData.double4D.toDouble3D(),
            targetInt3D.toDouble3DCenter(),
            game.universeClient.getUniverseData3D().universeSettings.speedOfLight
        )

        targetVelocityXTextField.text = targetVelocity.vx.toString().take(6)
        targetVelocityYTextField.text = targetVelocity.vy.toString().take(6)
        targetVelocityZTextField.text = targetVelocity.vz.toString().take(6)
    }

    override fun onSelectedPlayerIdListChange() {
        if (game.universeClient.newSelectedPlayerId != playerData.id) {

            val targetDouble3D: Double3D = game.universeClient.getUniverseData3D().get(
                game.universeClient.newSelectedPlayerId
            ).double4D.toDouble3D()

            val targetVelocity = displacementToVelocity(
                playerData.double4D.toDouble3D(),
                targetDouble3D,
                game.universeClient.getUniverseData3D().universeSettings.speedOfLight
            )

            targetVelocityXTextField.text = targetVelocity.vx.toString().take(6)
            targetVelocityYTextField.text = targetVelocity.vy.toString().take(6)
            targetVelocityZTextField.text = targetVelocity.vz.toString().take(6)
        }
    }


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().get(game.universeClient.getUniverseData3D().id)
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Physics: player ${playerData.id}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        val massLabel = createLabel(
            "Core rest mass: ${playerData.playerInternalData.physicsData.coreRestMass}",
            gdxSettings.smallFontSize
        )

        table.add(massLabel)

        table.row().space(10f)

        val energyLabel = createLabel(
            "Fuel rest mass: ${playerData.playerInternalData.physicsData.fuelRestMass.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        table.add(energyLabel)

        table.row().space(10f)

        val moveMaxPowerLabel = createLabel(
            "Max. fuel rest mass change: ${playerData.playerInternalData.physicsData.maxDeltaFuelRestMass.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        table.add(moveMaxPowerLabel)

        table.row().space(10f)

        table.add(createDouble4DTable())

        table.row().space(10f)

        table.add(createVelocityTable())

        table.row().space(20f)

        table.add(createChangeVelocityTable())
    }

    private fun createDouble4DTable(): Table {
        val nestedTable: Table = Table()

        val double4DTLabel = createLabel(
            "t: ${playerData.double4D.t.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DTLabel).space(10f)


        val double4DXLabel = createLabel(
            "x: ${playerData.double4D.x.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DXLabel).space(10f)

        val double4DYLabel = createLabel(
            "y: ${playerData.double4D.y.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DYLabel).space(10f)

        val double4DZLabel = createLabel(
            "z: ${playerData.double4D.z.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DZLabel).space(10f)

        return nestedTable
    }

    private fun createVelocityTable(): Table {
        val nestedTable: Table = Table()

        val velocityXLabel = createLabel(
            "vx: ${playerData.velocity.vx.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityXLabel).space(10f)


        val velocityYLabel = createLabel(
            "vy: ${playerData.velocity.vy.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityYLabel).space(10f)

        val velocityZLabel = createLabel(
            "vz: ${playerData.velocity.vz.toString().take(6)}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityZLabel).space(10f)

        return nestedTable
    }

    fun createChangeVelocityTable(): Table {
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

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}