package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
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
                    "War type: ${if (warState.isOffensive) {"Offensive"} else {"Defensive"}}",
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
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val currentPlayerData: PlayerData = game.universeClient.getCurrentPlayerData()

            // Target the highest possible leader
            val targetPlayerId: Int = playerData.playerInternalData.leaderIdList.firstOrNull {
                !currentPlayerData.isLeaderOrSelf(it)
            } ?: playerData.playerId

            val declareWarCommand = DeclareWarCommand(
                toId = targetPlayerId,
                fromId = currentPlayerData.playerId,
                fromInt4D = currentPlayerData.int4D,
                senderLeaderIdList = currentPlayerData.playerInternalData.leaderIdList,
            )

            game.universeClient.currentCommand = declareWarCommand
        }
        nestedTable.add(declareWarButton)

        nestedTable.row().space(10f)

        val declareIndependenceToDirectLeaderButton = createTextButton(
            "Declare independence (direct leader)",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val declareIndependenceToDirectLeaderCommand = DeclareIndependenceToDirectLeaderCommand(
                toId = playerData.playerInternalData.directLeaderId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = declareIndependenceToDirectLeaderCommand
        }
        nestedTable.add(declareIndependenceToDirectLeaderButton)

        nestedTable.row().space(10f)

        val declareIndependenceToTopLeaderButton = createTextButton(
            "Declare independence (top leader)",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val declareIndependenceToTopLeaderCommand = DeclareIndependenceToTopLeaderCommand(
                toId = playerData.topLeaderId(),
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = declareIndependenceToTopLeaderCommand
        }
        nestedTable.add(declareIndependenceToTopLeaderButton)

        nestedTable.row().space(10f)

        val proposePeaceButton = createTextButton(
            "Propose peace",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
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

        nestedTable.row().space(10f)

        val surrenderButton = createTextButton(
            "Surrender",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val surrenderCommand = SurrenderCommand(
                toId = game.universeClient.getCurrentPlayerData().playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetPlayerId = otherPlayerId.value
            )

            game.universeClient.currentCommand = surrenderCommand
        }
        nestedTable.add(surrenderButton)

        return nestedTable
    }
}