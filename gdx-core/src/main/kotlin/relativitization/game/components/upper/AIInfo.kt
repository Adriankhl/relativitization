package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.ai.AI
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name
import relativitization.universe.data.commands.Command

class AIInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var aiName: String = DefaultAI.name()

    private val aiCommandList: MutableList<Command> = mutableListOf()

    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)


        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel(
            "AI: Player ${game.universeClient.universeClientSettings.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        table.add(createAISelectionTable())
    }

    private fun createAISelectionTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "AI: ",
                gdxSettings.smallFontSize,
            )
        )

        val aiSelectBox = createSelectBox(
            AI::class.sealedSubclasses.map { it.objectInstance!!.name() },
            DefaultAI.name(),
            gdxSettings.smallFontSize
        ) { s, _ ->
            aiName = s
        }
        nestedTable.add(aiSelectBox)

        return nestedTable
    }

    private fun createAICommandTable(index: Int): Table {
        val nestedTable = Table()

        return if ((index >= 0) && (index < aiCommandList.size)) {
            val command: Command = aiCommandList[index]

            nestedTable
        } else {
            nestedTable
        }
    }
}