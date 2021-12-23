package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.ai.AI
import relativitization.universe.ai.AICollection
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.name

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

        table.row().space(10f)

        table.add(createAIComputeTable())

        table.row().space(20f)

        table.add(createAllAICommandTable()).growX()
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
            aiName,
            gdxSettings.smallFontSize
        ) { s, _ ->
            aiName = s
        }
        nestedTable.add(aiSelectBox)

        return nestedTable
    }

    private fun createAIComputeTable(): Table {
        val nestedTable = Table()

        val computeButton = createTextButton(
            "Compute",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val commandList: List<Command> = AICollection.compute(
                game.universeClient.getUniverseData3D(),
                aiName
            )

            aiCommandList.clear()

            aiCommandList.addAll(commandList)

            updateTable()
        }
        nestedTable.add(computeButton)

        nestedTable.row().space(20f)

        val useAllButton = createTextButton(
            "Use all",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.universeClient.clearCommandList()

            aiCommandList.forEach {
                game.universeClient.currentCommand = it
                game.universeClient.confirmCurrentCommand()
            }
        }
        nestedTable.add(useAllButton)

        return nestedTable
    }

    private fun createAllAICommandTable(): Table {
        val nestedTable = Table()

        aiCommandList.forEach {
            nestedTable.add(createAICommandTable(it)).growX()

            nestedTable.row().space(20f)
        }

        return nestedTable
    }

    private fun createAICommandTable(command: Command): Table {
        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val commandNameLabel = createLabel(command.name(), gdxSettings.normalFontSize)
        nestedTable.add(commandNameLabel).growX().colspan(2)

        nestedTable.row().space(10f)

        val commandDescriptionLabel = createLabel(command.description(), gdxSettings.smallFontSize)
        commandDescriptionLabel.wrap = true
        nestedTable.add(commandDescriptionLabel).growX().colspan(2)

        nestedTable.row().space(10f)

        val addButton = createTextButton(
            "Add",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            aiCommandList.remove(command)
            game.universeClient.currentCommand = command
            game.universeClient.confirmCurrentCommand()
            updateTable()
        }

        nestedTable.add(addButton)

        val cancelButton = createTextButton(
            "Remove",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            aiCommandList.remove(command)
            updateTable()
        }

        nestedTable.add(cancelButton)

        return nestedTable
    }
}