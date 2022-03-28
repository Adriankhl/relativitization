package relativitization.game.components.upper.defaults

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.name

class EventsInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Events"

    override val infoPriority: Int = 5

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

    private fun updatePlayerData() {
        playerData = game.universeClient.getValidPrimaryPlayerData()
    }

    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Event list: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

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

        val eventNameLabel = createLabel(
            eventData.event.name(),
            gdxSettings.normalFontSize
        )
        nestedTable.add(eventNameLabel).colspan(2)

        nestedTable.row().space(10f)

        val stayTimeLabel = createLabel(
            "Stay time: ${eventData.event.stayTime}  Counter: ${eventData.eventRecordData.stayCounter}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(stayTimeLabel).colspan(2)

        nestedTable.row().space(10f)

        val eventDescriptionLabel = createLabel(
            eventData.event.description(),
            gdxSettings.smallFontSize
        )
        eventDescriptionLabel.wrap = true
        nestedTable.add(eventDescriptionLabel).colspan(2).growX()

        nestedTable.row().space(10f)

        for (choice in eventData.event.choiceDescription) {
            val selectChoiceButton = createTextButton(
                "${choice.key}",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                commandButtonColor,
            ) {
                val selectEventDataCommand = SelectEventChoiceCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getUniverseData3D().id,
                    fromInt4D = game.universeClient.getUniverseData3D()
                        .getCurrentPlayerData().int4D,
                    eventKey = eventKey,
                    eventName = eventData.event.name(),
                    choice = choice.key
                )

                game.universeClient.currentCommand = selectEventDataCommand
            }

            nestedTable.add(selectChoiceButton)

            val choiceDescriptionLabel = createLabel(choice.value, gdxSettings.smallFontSize)
            choiceDescriptionLabel.wrap = true
            nestedTable.add(choiceDescriptionLabel).growX()

            nestedTable.row().space(10f)
        }

        val selectedChoiceLabel = if (eventData.eventRecordData.hasChoice) {
            createLabel(
                "Selected choice: ${eventData.eventRecordData.choice}",
                gdxSettings.smallFontSize
            )
        } else {
            createLabel("Selected choice: default", gdxSettings.smallFontSize)
        }
        nestedTable.add(selectedChoiceLabel).colspan(2)

        return nestedTable
    }
}