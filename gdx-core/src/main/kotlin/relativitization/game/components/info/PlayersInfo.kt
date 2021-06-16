package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData

class PlayersInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    // Viewed history of player in Id
    private val viewedPlayerIdList: MutableList<Int> = mutableListOf(-1)

    // Id of currently viewing player
    private var viewingIdIndex: Int = 0

    // the currently viewing player data
    private var playerData: PlayerData = PlayerData(-1)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        viewedPlayerIdList.clear()
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
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
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }

        // If this id has not been stored, clear later index and add this id
        if (viewingIdIndex <= viewedPlayerIdList.size - 2) {
            if (viewedPlayerIdList[viewingIdIndex] != playerData.id) {
                viewingIdIndex++
                viewedPlayerIdList.subList(viewingIdIndex, viewedPlayerIdList.size).clear()
                viewedPlayerIdList.add(playerData.id)
            }
        } else {
            viewingIdIndex = viewedPlayerIdList.size
            viewedPlayerIdList.add(playerData.id)
        }
    }


    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Selected: player ${playerData.id}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        table.add(createPlayerImageAndButtonTable())

        table.row().space(10f)

        table.add(createAllPlayerIdTable())

        table.row().space(10f)

        table.add(createDirectLeaderIdTable())

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
            assets = assets,
            width = 128f,
            height = 128f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            game.universeClient.mapCenterPlayerId = playerData.id
        }

        nestedTable.add(previousPlayerIdButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        nestedTable.add(playerImageStack).size(128f * gdxSettings.imageScale, 128f * gdxSettings.imageScale)

        nestedTable.add(nextPlayerIdButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        return nestedTable
    }

    private fun createAllPlayerIdTable(): Table {
        val nestedTable = Table()

        val allPlayerIdLabel = createLabel("Available Player Id: ", gdxSettings.smallFontSize)

        val allPlayerIdSelectBox = createSelectBox(
            itemList = game.universeClient.getUniverseData3D().playerDataMap.keys.toList().sorted(),
            default = playerData.id,
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

        val directLeaderIdLabel = createLabel("DirectLeader Id: ", gdxSettings.smallFontSize)

        val directLeaderButton = createTextButton(
            text = playerData.playerInternalData.directLeaderId.toString(),
            fontSize = gdxSettings.smallFontSize,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {
            game.universeClient.primarySelectedPlayerId = playerData.playerInternalData.directLeaderId
        }

        nestedTable.add(directLeaderIdLabel)
        nestedTable.add(directLeaderButton)

        return nestedTable
    }
}