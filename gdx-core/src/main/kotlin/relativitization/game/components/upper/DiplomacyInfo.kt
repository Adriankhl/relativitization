package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.defaults.diplomacy.WarStateData

class DiplomacyInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // For choosing the diplomatic relation between player with this Id and the primary player
    // Update by select box or select new player
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
    }

    private fun createSelectOtherPlayerTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Other player Id: ${otherPlayerId}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val otherPlayerIdSelectBox = createSelectBox(
            (playerData.playerInternalData.diplomacyData().relationMap.keys +
                    playerData.playerInternalData.diplomacyData().warData.warStateMap.keys).toList(),
            otherPlayerId,
            gdxSettings.smallFontSize
        ) { i, _ ->
            otherPlayerId = i
        }
        nestedTable.add(otherPlayerIdSelectBox).colspan(2)

        return nestedTable
    }

    private fun createDiplomaticRelationTable(): Table {
        val nestedTable = Table()

        val diplomaticRelationData: DiplomaticRelationData =
            playerData.playerInternalData.diplomacyData().getDiplomaticRelationData(otherPlayerId)

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

        return nestedTable
    }

    private fun createWarStateTable(): Table {
        val nestedTable = Table()

        if (playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(otherPlayerId)) {
            val warState: WarStateData =
                playerData.playerInternalData.diplomacyData().warData.getWarStateData(otherPlayerId)

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
}