package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.modifier.CombatModifierData
import relativitization.universe.data.components.defaults.modifier.DiplomacyModifierData
import relativitization.universe.data.components.defaults.modifier.PhysicsModifierData

class ModifierInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    private var otherPlayerId = createIntTextField(
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

        table.add(
            createLabel(
                "Modifier: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        )

        table.row().spaceTop(30f)

        table.add(createPhysicsModifierTable())

        table.row().spaceTop(30f)

        table.add(createCombatModifierTable())

        table.row().spaceTop(30f)

        table.add(createDiplomacyModifierTable())
    }

    private fun createPhysicsModifierTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Physics modifier: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(20f)

        val physicsModifier: PhysicsModifierData = playerData.playerInternalData.modifierData()
            .physicsModifierData

        nestedTable.add(
            createLabel(
            "Disable fuel increase: ${physicsModifier.disableRestMassIncreaseTimeLimit}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createCombatModifierTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Combat modifier: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(20f)

        val combatModifier: CombatModifierData = playerData.playerInternalData.modifierData()
            .combatModifierData

        nestedTable.add(
            createLabel(
            "Disable military base recovery: ${combatModifier.disableMilitaryBaseRecoveryTimeLimit}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createDiplomacyModifierTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Diplomacy modifier: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(20f)

        val diplomacyModifier: DiplomacyModifierData = playerData.playerInternalData.modifierData()
            .diplomacyModifierData

        val otherPlayerIdSelectBox = createSelectBox(
            (diplomacyModifier.peaceTreaty.keys +
                    diplomacyModifier.relationModifierMap.keys).toList(),
            otherPlayerId.value,
            gdxSettings.smallFontSize
        ) { i, _ ->
            otherPlayerId.value = i
            updateTable()
        }
        nestedTable.add(otherPlayerIdSelectBox).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Peace treaty: ${diplomacyModifier.getPeaceTreatyLength(otherPlayerId.value)}",
                gdxSettings.smallFontSize
            )
        )

        diplomacyModifier.getRelationModifier(otherPlayerId.value).receiveFuelList.forEach {
            nestedTable.row().spaceTop(20f)

            nestedTable.add(
                createLabel(
                    "Received fuel: ${it.durationLeft}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Relation change: ${it.change}",
                    gdxSettings.smallFontSize,
                )
            )
        }

        return nestedTable
    }
}