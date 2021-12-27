package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.DeclareIndependenceCommand
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.commands.ProposePeaceCommand
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.defaults.diplomacy.WarStateData

class DiplomacyInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // For choosing the diplomatic relation between player with this Id and the primary player
    // Update by select box or select new player
    private val otherPlayerId = createIntTextField(
        -1,
        gdxSettings.smallFontSize
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
        otherPlayerId.value = game.universeClient.newSelectedPlayerId
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
            "Diplomacy: player ${playerData.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(10f)

        table.add(createSelectOtherPlayerTable())

        table.row().space(20f)

        table.add(createDiplomaticRelationTable())

        table.row().space(20f)

        table.add(createWarStateTable())

        table.row().space(20f)

        table.add(createWarCommandTable())
    }

    private fun createSelectOtherPlayerTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Other player Id: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(otherPlayerId.textField)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "In-relation player",
                gdxSettings.smallFontSize
            )
        )

        val otherPlayerIdSelectBox = createSelectBox(
            (playerData.playerInternalData.diplomacyData().relationMap.keys +
                    playerData.playerInternalData.diplomacyData().warData.warStateMap.keys +
                    playerData.playerId).toList(),
            otherPlayerId.value,
            gdxSettings.smallFontSize
        ) { i, _ ->
            otherPlayerId.value = i
            updateTable()
        }
        nestedTable.add(otherPlayerIdSelectBox).colspan(2)

        return nestedTable
    }

    private fun createDiplomaticRelationTable(): Table {
        val nestedTable = Table()

        // Only show this information if the other player is not self
        if (playerData.playerId != otherPlayerId.value) {
            val diplomaticRelationData: DiplomaticRelationData =
                playerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(otherPlayerId.value)

            nestedTable.add(
                createLabel(
                    "Relation: ${diplomaticRelationData.relation}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Diplomatic state: ${diplomaticRelationData.diplomaticRelationState}",
                    gdxSettings.smallFontSize
                )
            )
        }

        return nestedTable
    }

    private fun createWarStateTable(): Table {
        val nestedTable = Table()

        if (playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                otherPlayerId.value
            )
        ) {
            val warState: WarStateData =
                playerData.playerInternalData.diplomacyData().warData.getWarStateData(otherPlayerId.value)

            nestedTable.add(
                createLabel(
                    "In war",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "War start time: ${warState.startTime}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Proposed peace: ${warState.proposePeace}",
                    gdxSettings.smallFontSize
                )
            )
        }


        return nestedTable
    }

    private fun createWarCommandTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "War commands: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        val declareWarButton = createTextButton(
            "Declare war",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val declareWarCommand = DeclareWarCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = declareWarCommand
        }
        nestedTable.add(declareWarButton)

        nestedTable.row().space(10f)

        val declareIndependenceButton = createTextButton(
            "Declare independence",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val declareIndependenceCommand = DeclareIndependenceCommand(
                toId = playerData.topLeaderId(),
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = declareIndependenceCommand
        }
        nestedTable.add(declareIndependenceButton)

        nestedTable.row().space(10f)

        val proposePeaceButton = createTextButton(
            "Propose peace",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val proposePeaceCommand = ProposePeaceCommand(
                toId = game.universeClient.getCurrentPlayerData().playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetPlayerId = otherPlayerId.value
            )

            game.universeClient.currentCommand = proposePeaceCommand
        }
        nestedTable.add(proposePeaceButton)

        return nestedTable
    }
}