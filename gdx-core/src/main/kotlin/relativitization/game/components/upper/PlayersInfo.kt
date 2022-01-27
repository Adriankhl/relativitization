package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage
import relativitization.game.utils.PlayerSummary
import relativitization.game.utils.ScreenComponent
import relativitization.game.utils.Summary
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType

class PlayersInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    // Viewed history of player in Id
    private val viewedPlayerIdList: MutableList<Int> = mutableListOf(-1)

    // ID of currently viewing player
    private var viewingIdIndex: Int = 0

    // the currently viewing player data
    private var playerData: PlayerData = PlayerData(-1)

    // cache the computed summary
    private var showPlayerSummary: Boolean = false
    private var playerSummary: PlayerSummary = PlayerSummary()
    private var playerSummaryOption: PlayerSummaryOption = PlayerSummaryOption.SELF_ONLY
    private var selectedPlayerSummaryResourceType: ResourceType = ResourceType.PLANT

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        viewedPlayerIdList.clear()
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onUniverseData3DChange() {
        // Clear data list when change data
        viewedPlayerIdList.clear()
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerDataAndIdList()
        updateTable()
    }

    private fun previousPlayerId() {
        if (viewingIdIndex >= 1) {
            viewingIdIndex--
            game.universeClient.primarySelectedPlayerId = viewedPlayerIdList[viewingIdIndex]
        }
    }

    private fun nextPlayerId() {
        if (viewingIdIndex <= viewedPlayerIdList.size - 2) {
            viewingIdIndex++
            game.universeClient.primarySelectedPlayerId = viewedPlayerIdList[viewingIdIndex]
        }
    }


    private fun updatePlayerDataAndIdList() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getCurrentPlayerData()
        }

        // If this id has not been stored, clear later index and add this id
        if (viewingIdIndex <= viewedPlayerIdList.size - 2) {
            if (viewedPlayerIdList[viewingIdIndex] != playerData.playerId) {
                viewingIdIndex++
                viewedPlayerIdList.subList(viewingIdIndex, viewedPlayerIdList.size).clear()
                viewedPlayerIdList.add(playerData.playerId)
            }
        } else {
            viewingIdIndex = viewedPlayerIdList.size
            viewedPlayerIdList.add(playerData.playerId)
        }
    }


    private fun updateTable() {
        table.clear()

        val headerLabel =
            createLabel("Selected: player ${playerData.playerId}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        table.add(createPlayerImageAndButtonTable())

        table.row().space(10f)

        table.add(createAllPlayerIdTable())

        table.row().space(10f)

        table.add(createDirectLeaderIdTable())

        table.row().space(10f)

        table.add(createDirectSubordinateIdTable())

        table.row().space(10f)

        table.add(createLeaderIdTable())

        table.row().space(10f)

        table.add(createSubordinateIdTable())

        table.row().space(10f)
    }

    private fun createPlayerImageAndButtonTable(): Table {
        val nestedTable = Table()

        val previousPlayerIdButton = createImageButton(
            name = "basic/white-left-arrow",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            previousPlayerId()
        }

        val nextPlayerIdButton = createImageButton(
            name = "basic/white-right-arrow",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            nextPlayerId()
        }


        val playerImageStack = PlayerImage.getPlayerImageStack(
            playerData = playerData,
            universeData3DAtPlayer = game.universeClient.getUniverseData3D(),
            primaryPlayerData = game.universeClient.getPrimarySelectedPlayerData(),
            assets = assets,
            width = 128f,
            height = 128f,
            soundVolume = gdxSettings.soundEffectsVolume,
            mapPlayerColorMode = gdxSettings.mapPlayerColorMode
        ) {
            game.universeClient.mapCenterPlayerId = playerData.playerId
            game.universeClient.primarySelectedInt3D = playerData.int4D.toInt3D()
        }

        nestedTable.add(previousPlayerIdButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        nestedTable.add(playerImageStack)
            .size(128f * gdxSettings.imageScale, 128f * gdxSettings.imageScale)

        nestedTable.add(nextPlayerIdButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        return nestedTable
    }

    private fun createAllPlayerIdTable(): Table {
        val nestedTable = Table()

        val allPlayerIdLabel = createLabel("Available Player Id: ", gdxSettings.smallFontSize)

        val allPlayerIdSelectBox = createSelectBox(
            itemList = game.universeClient.getUniverseData3D().playerDataMap.keys.toList().sorted(),
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.primarySelectedPlayerId = selectedId
        }

        nestedTable.add(allPlayerIdLabel)
        nestedTable.add(allPlayerIdSelectBox)

        return nestedTable
    }

    private fun createDirectLeaderIdTable(): Table {
        val nestedTable = Table()

        val directLeaderIdLabel = createLabel("Direct leader Id: ", gdxSettings.smallFontSize)

        val directLeaderButton = createTextButton(
            text = playerData.playerInternalData.directLeaderId.toString(),
            fontSize = gdxSettings.smallFontSize,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {
            game.universeClient.primarySelectedPlayerId =
                playerData.playerInternalData.directLeaderId
        }

        nestedTable.add(directLeaderIdLabel)
        nestedTable.add(directLeaderButton)

        return nestedTable
    }

    private fun createDirectSubordinateIdTable(): Table {
        val nestedTable = Table()

        val directSubordinateIdLabel =
            createLabel("Direct subordinate Id: ", gdxSettings.smallFontSize)

        val directSubordinateIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.directSubordinateIdList,
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.primarySelectedPlayerId = selectedId
        }

        nestedTable.add(directSubordinateIdLabel)
        nestedTable.add(directSubordinateIdSelectBox)

        return nestedTable
    }

    private fun createLeaderIdTable(): Table {
        val nestedTable = Table()

        val leaderIdLabel = createLabel("Leader Id: ", gdxSettings.smallFontSize)

        val leaderIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.leaderIdList,
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.primarySelectedPlayerId = selectedId
        }

        nestedTable.add(leaderIdLabel)
        nestedTable.add(leaderIdSelectBox)

        return nestedTable
    }

    private fun createSubordinateIdTable(): Table {
        val nestedTable = Table()

        val subordinateIdLabel = createLabel("Subordinate Id: ", gdxSettings.smallFontSize)

        val subordinateIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.subordinateIdList,
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.primarySelectedPlayerId = selectedId
        }

        nestedTable.add(subordinateIdLabel)
        nestedTable.add(subordinateIdSelectBox)

        return nestedTable
    }

    fun updatePlayerSummary() {
        val otherPlayerIdList: List<Int> = when (playerSummaryOption) {
            PlayerSummaryOption.SELF_ONLY -> listOf()
            PlayerSummaryOption.SELF_AND_SUBORDINATES -> game.universeClient.getUniverseData3D().get(
                game.universeClient.primarySelectedPlayerId
            ).playerInternalData.subordinateIdList
            PlayerSummaryOption.SELECTED -> game.universeClient.selectedPlayerIdList
        }
        playerSummary = Summary.computeFromUniverseData3DAtPlayer(
            game.universeClient.primarySelectedPlayerId,
            otherPlayerIdList,
            game.universeClient.getUniverseData3D(),
        )
    }
}

enum class PlayerSummaryOption(private val value: String) {
    SELF_ONLY("Self only"),
    SELF_AND_SUBORDINATES("Self and subordinates"),
    SELECTED("Selected"),
    ;

    override fun toString(): String {
        return value
    }
}