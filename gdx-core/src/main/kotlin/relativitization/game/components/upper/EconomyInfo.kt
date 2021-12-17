package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.TransferFuelToMovementCommand
import relativitization.universe.data.commands.TransferFuelToProductionCommand
import relativitization.universe.data.commands.TransferFuelToTradeCommand

class EconomyInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    private var otherPlayerId: Int = -1


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

    override fun onSelectedPlayerIdListChange() {
        otherPlayerId = game.universeClient.newSelectedPlayerId
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getCurrentPlayerData()
        }
    }


    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Economy: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(20f)

        table.add(createFuelRestMassTable())

        table.row().space(20f)
    }

    private fun createFuelRestMassTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Fuel rest mass data: ",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Storage: ${playerData.playerInternalData.physicsData().fuelRestMassData.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Movement: ${playerData.playerInternalData.physicsData().fuelRestMassData.movement}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToMovementSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToMovementButton = createTextButton(
            "Transfer to movement",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToMovementCommand = TransferFuelToMovementCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToMovementSlider.value
            )

            game.universeClient.currentCommand = transferFuelToMovementCommand
        }
        nestedTable.add(transferToMovementButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToMovementSlider).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Production: ${playerData.playerInternalData.physicsData().fuelRestMassData.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToProductionSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToProductionButton = createTextButton(
            "Transfer to production",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToProductionCommand = TransferFuelToProductionCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToMovementSlider.value
            )

            game.universeClient.currentCommand = transferFuelToProductionCommand
        }
        nestedTable.add(transferToProductionButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToProductionSlider).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Trade: ${playerData.playerInternalData.physicsData().fuelRestMassData.trade}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToTradeSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToTradeButton = createTextButton(
            "Transfer to trade",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToTradeCommand = TransferFuelToTradeCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToMovementSlider.value
            )

            game.universeClient.currentCommand = transferFuelToTradeCommand
        }
        nestedTable.add(transferToTradeButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToTradeSlider).colspan(2)

        return nestedTable
    }
}