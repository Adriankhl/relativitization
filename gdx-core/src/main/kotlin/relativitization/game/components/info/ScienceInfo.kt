package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.subsystem.science.knowledge.AppliedResearchData
import relativitization.universe.data.subsystem.science.knowledge.BasicResearchData
import relativitization.universe.data.subsystem.science.knowledge.KnowledgeData

class ScienceInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

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

        val headerLabel =
            createLabel("Science: player ${playerData.playerId}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        val playerKnowledgeDataLabel = createLabel("Common Sense", gdxSettings.normalFontSize)
        table.add(playerKnowledgeDataLabel)

        table.row().space(10f)

        table.add(createKnowledgeDataTable(playerData.playerInternalData.playerScienceData().playerKnowledgeData))

        table.row().space(20f)

        val commonSenseLabel = createLabel("Common Sense", gdxSettings.normalFontSize)
        table.add(commonSenseLabel)

        table.row().space(10f)

        table.add(createKnowledgeDataTable(playerData.playerInternalData.playerScienceData().commonSenseKnowledgeData))
    }

    private fun createKnowledgeDataTable(knowledgeData: KnowledgeData): Table {
        val nestedTable: Table = Table()

        nestedTable.add(createBasicResearchDataTable(knowledgeData.basicResearchData))

        nestedTable.row().space(20f)

        nestedTable.add(createAppliedResearchDataTable(knowledgeData.appliedResearchData))

        return nestedTable
    }

    /**
     * Create a table displaying basic research data
     *
     * @param basicResearchData the data to be displayed
     */
    private fun createBasicResearchDataTable(basicResearchData: BasicResearchData): Table {
        val nestedTable: Table = Table()

        val headerLabel = createLabel("Basic Research", gdxSettings.normalFontSize)
        nestedTable.add(headerLabel)

        nestedTable.row().space(10f)

        val mathematicsLevelLabel = createLabel(
            "Mathematics Level: ${basicResearchData.mathematicsLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(mathematicsLevelLabel)

        nestedTable.row().space(10f)

        val physicsLevelLabel = createLabel(
            "Physics Level: ${basicResearchData.physicsLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(physicsLevelLabel)

        nestedTable.row().space(10f)

        val computerScienceLevelLabel = createLabel(
            "Computer Science Level: ${basicResearchData.computerScienceLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(computerScienceLevelLabel)

        nestedTable.row().space(10f)

        val lifeScienceLevelLabel = createLabel(
            "Life Science Level: ${basicResearchData.lifeScienceLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(lifeScienceLevelLabel)

        nestedTable.row().space(10f)

        val socialScienceLevelLabel = createLabel(
            "Social Science Level: ${basicResearchData.socialScienceLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(socialScienceLevelLabel)

        nestedTable.row().space(10f)

        val humanityLevelLabel = createLabel(
            "Humanity level: ${basicResearchData.humanityLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(humanityLevelLabel)

        return nestedTable
    }


    /**
     * Create a table displaying applied research data
     *
     * @param appliedResearchData the data to be displayed
     */
    private fun createAppliedResearchDataTable(appliedResearchData: AppliedResearchData): Table {
        val nestedTable: Table = Table()

        val headerLabel = createLabel("Applied Research", gdxSettings.normalFontSize)
        nestedTable.add(headerLabel)

        nestedTable.row().space(10f)

        val energyTechnologyLevelLabel = createLabel(
            "Energy Technology level: ${appliedResearchData.energyTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(energyTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val foodTechnologyLevelLabel = createLabel(
            "Food Technology level: ${appliedResearchData.foodTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(foodTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val biomedicalTechnologyLevelLabel = createLabel(
            "Biomedical Technology level: ${appliedResearchData.biomedicalTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(biomedicalTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val chemicalTechnologyLevelLabel = createLabel(
            "Chemical Technology level: ${appliedResearchData.chemicalTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(chemicalTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val environmentalTechnologyLevelLabel = createLabel(
            "Environmental Technology level: ${appliedResearchData.environmentalTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(environmentalTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val architectureTechnologyLevelLabel = createLabel(
            "Architecture Technology level: ${appliedResearchData.architectureTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(architectureTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val machineryTechnologyLevelLabel = createLabel(
            "Machinery Technology level: ${appliedResearchData.machineryTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(machineryTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val materialTechnologyLevelLabel = createLabel(
            "Material Technology level: ${appliedResearchData.materialTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(materialTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val informationTechnologyLevelLabel = createLabel(
            "Information Technology level: ${appliedResearchData.informationTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(informationTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val artTechnologyLevelLabel = createLabel(
            "Art Technology level: ${appliedResearchData.artTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(artTechnologyLevelLabel)

        nestedTable.row().space(10f)

        val militaryTechnologyLevelLabel = createLabel(
            "Military Technology level: ${appliedResearchData.militaryTechnologyLevel}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(militaryTechnologyLevelLabel)

        nestedTable.row().space(10f)

        return nestedTable
    }
}