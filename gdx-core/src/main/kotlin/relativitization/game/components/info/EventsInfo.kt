package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
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

        table.row().space(20f)

        for (eventData in playerData.playerInternalData.eventDataList) {
            val eventDataTable = createEventTable(eventData)
            table.add(eventDataTable)

            table.row().space(20f)
        }
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

        for (choice in eventData.event.choiceDescription) {
            nestedTable.row().space(10f)

            val selectChoiceButton = createTextButton(
                "Select choice ${choice.key}",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume
            ) {
                val selectEventDataCommand = SelectEventChoiceCommand(
                    eventIndex = playerData.playerInternalData.eventDataList.indexOf(eventData),
                    eventName = eventData.event.name,
                    choice = choice.key,
                    fromId = game.universeClient.getUniverseData3D().id,
                    fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                    toId = playerData.id
                )

                val canSend = selectEventDataCommand.canSendFromPlayer(
                    game.universeClient.getUniverseData3D().getCurrentPlayerData(),
                    game.universeClient.getUniverseData3D().universeSettings
                )

                if (canSend) {
                    game.universeClient.currentCommand = selectEventDataCommand
                } else {
                    game.universeClient.currentCommand = CannotSendCommand()
                }
            }

            nestedTable.add(selectChoiceButton)

            nestedTable.row().space(10f)

            val choiceDescriptionLabel = createLabel(choice.value, gdxSettings.smallFontSize)
            nestedTable.add(choiceDescriptionLabel)
        }

        return nestedTable
    }
}