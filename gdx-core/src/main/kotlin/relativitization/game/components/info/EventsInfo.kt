package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.events.EventData

class EventsInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private var playerData: PlayerData = PlayerData(-1)

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

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Event list: player ${playerData.id}", gdxSettings.bigFontSize)

        table.add(headerLabel)
    }

    private fun createEventTable(eventData: EventData): Table {
        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val eventNameLabel = createLabel(eventData.event.name, gdxSettings.normalFontSize)
        nestedTable.add(eventNameLabel)

        nestedTable.row().space(10f)

        val eventDescriptionLabel = createLabel(eventData.event.description, gdxSettings.smallFontSize)
        eventDescriptionLabel.wrap = true
        nestedTable.add(eventDescriptionLabel)

        nestedTable.row().space(10f)

        return nestedTable
    }
}