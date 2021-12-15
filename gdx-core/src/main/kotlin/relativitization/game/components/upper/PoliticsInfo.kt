package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.events.AskToMergeCarrierEvent

class PoliticsInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)


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

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getCurrentPlayerData()
        }
    }


    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel(
            "Politics: player ${playerData.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        table.add(createMergePlayerTable())
    }

    private fun createMergePlayerTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Waiting to merge: ${playerData.playerInternalData.politicsData().agreeMerge}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val askToMergeButton = createTextButton(
            "Ast to merge",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val askToMergeEvent = AskToMergeCarrierEvent(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
            )

            val addEventCommand = AddEventCommand(
                event = askToMergeEvent,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D
            )

            game.universeClient.currentCommand = addEventCommand
        }
        nestedTable.add(askToMergeButton)

        return nestedTable
    }
}