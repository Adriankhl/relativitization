package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.name

class EventsInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    init {
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

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Event list: player ${playerData.playerId}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        for (eventKey in playerData.playerInternalData.eventDataMap.keys) {
            val eventDataTable = createEventTable(eventKey)
            table.add(eventDataTable).growX()

            table.row().space(20f)
        }
    }

    private fun createEventTable(eventKey: Int): Table {
        val eventData: EventData = playerData.playerInternalData.eventDataMap.getValue(eventKey)

        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val eventNameLabel = createLabel(eventData.event.name(), gdxSettings.normalFontSize)
        nestedTable.add(eventNameLabel).colspan(2)

        nestedTable.row().space(10f)

        val eventDescriptionLabel = createLabel(eventData.event.description, gdxSettings.smallFontSize)
        eventDescriptionLabel.wrap = true
        nestedTable.add(eventDescriptionLabel).colspan(2).growX()

        nestedTable.row().space(10f)

        val selectedChoiceLabel = if (eventData.hasChoice) {
            createLabel("Selected choice: ${eventData.choice}", gdxSettings.smallFontSize)
        } else {
            createLabel("Selected choice: default", gdxSettings.smallFontSize)
        }
        nestedTable.add(selectedChoiceLabel).colspan(2)

        nestedTable.row().space(10f)

        for (choice in eventData.event.choiceDescription) {
            val selectChoiceButton = createTextButton(
                "${choice.key}",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume
            ) {
                val selectEventDataCommand = SelectEventChoiceCommand(
                    eventKey = eventKey,
                    eventName = eventData.event.name(),
                    choice = choice.key,
                    fromId = game.universeClient.getUniverseData3D().id,
                    fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                    toId = playerData.playerId
                )

                val canSend = selectEventDataCommand.canSendFromPlayer(
                    game.universeClient.planDataAtPlayer.thisPlayerData,
                    game.universeClient.getUniverseData3D().universeSettings
                )

                if (canSend) {
                    game.universeClient.currentCommand = selectEventDataCommand
                } else {
                    game.universeClient.currentCommand = CannotSendCommand()
                }
            }

            nestedTable.add(selectChoiceButton)

            val choiceDescriptionLabel = createLabel(choice.value, gdxSettings.smallFontSize)
            choiceDescriptionLabel.wrap = true
            nestedTable.add(choiceDescriptionLabel).growX()

            nestedTable.row().space(10f)
        }

        return nestedTable
    }
}