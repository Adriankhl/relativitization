package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.screens.NewUniverseScreen
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.physics.Velocity

class PhysicsInfo(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()
    private var scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    private val changeVelocityCommandButton = createImageButton(
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
        try {
            val vx = targetVelocityXTextField.text.toDouble()
            val vy = targetVelocityYTextField.text.toDouble()
            val vz = targetVelocityZTextField.text.toDouble()

            val changeVelocityCommand: ChangeVelocityCommand = ChangeVelocityCommand(
                game.universeClient.getUniverseData3D().getCurrentPlayerData().id,
                playerData.id,
                game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                Velocity(vx, vy, vz)
            )

            game.universeClient.currentCommand = changeVelocityCommand
        } catch (e: NumberFormatException) {
            logger.error("Invalid target velocity")
        }
    }

    private val targetVelocityXTextField = createTextField(
        "${playerData.playerInternalData.physicsData.velocity.vx}",
        gdxSettings.smallFontSize
    )

    private val targetVelocityYTextField = createTextField(
        "${playerData.playerInternalData.physicsData.velocity.vy}",
        gdxSettings.smallFontSize
    )

    private val targetVelocityZTextField = createTextField(
        "${playerData.playerInternalData.physicsData.velocity.vz}",
        gdxSettings.smallFontSize
    )

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)

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


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().get(game.universeClient.getUniverseData3D().id)
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Physics: player ${playerData.id}", gdxSettings.normalFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        val massLabel = createLabel(
            "Rest mass: ${playerData.playerInternalData.physicsData.restMass}",
            gdxSettings.smallFontSize
        )

        table.add(massLabel)

        table.row().space(10f)

        val energyLabel = createLabel(
            "Energy: ${playerData.playerInternalData.physicsData.energy}",
            gdxSettings.smallFontSize
        )

        table.add(energyLabel)

        table.row().space(10f)

        val moveEnergyEfficiencyLabel = createLabel(
            "Movement energy efficiency: ${playerData.playerInternalData.physicsData.moveEnergyEfficiency}",
            gdxSettings.smallFontSize
        )

        table.add(moveEnergyEfficiencyLabel)

        table.row().space(10f)

        val moveMaxPowerLabel = createLabel(
            "Movement max. power: ${playerData.playerInternalData.physicsData.moveMaxPower}",
            gdxSettings.smallFontSize
        )

        table.add(moveMaxPowerLabel)

        table.row().space(10f)

        table.add(createDouble4DTable())

        table.row().space(10f)

        table.add(createVelocityTable())
    }

    private fun createDouble4DTable(): Table {
        val nestedTable: Table = Table()

        val double4DTLabel = createLabel(
            "t: ${playerData.playerInternalData.physicsData.double4D.t}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DTLabel).space(10f)


        val double4DXLabel = createLabel(
            "x: ${playerData.playerInternalData.physicsData.double4D.x}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DXLabel).space(10f)

        val double4DYLabel = createLabel(
            "y: ${playerData.playerInternalData.physicsData.double4D.y}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DYLabel).space(10f)

        val double4DZLabel = createLabel(
            "y: ${playerData.playerInternalData.physicsData.double4D.y}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(double4DZLabel).space(10f)

        return nestedTable
    }

    private fun createVelocityTable(): Table {
        val nestedTable: Table = Table()

        val velocityXLabel = createLabel(
            "vx: ${playerData.playerInternalData.physicsData.velocity.vx}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityXLabel).space(10f)


        val velocityYLabel = createLabel(
            "vy: ${playerData.playerInternalData.physicsData.velocity.vy}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityYLabel).space(10f)

        val velocityZLabel = createLabel(
            "vz: ${playerData.playerInternalData.physicsData.velocity.vz}",
            gdxSettings.smallFontSize
        )

        nestedTable.add(velocityZLabel).space(10f)

        return nestedTable
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}