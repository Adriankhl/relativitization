package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryInternalData
import relativitization.universe.data.components.defaults.science.application.ScienceApplicationData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchData
import relativitization.universe.data.components.defaults.science.knowledge.KnowledgeData

class ScienceInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // Show common sense or player knowledge
    private var showCommonSenseInfo: Boolean = true
    private var showPlayerKnowledgeInfo: Boolean = false
    private var showScienceApplicationInfo: Boolean = false

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
                "Science: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        )

        table.row().space(20f)

        table.add(createInfoOptionTable())

        table.row().space(20f)

        if (showCommonSenseInfo) {
            table.add(
                createLabel(
                    "Common sense: ",
                    gdxSettings.normalFontSize
                )
            )

            table.row().space(10f)

            table.add(
                createKnowledgeDataTable(
                    playerData.playerInternalData.playerScienceData().commonSenseKnowledgeData
                )
            )

            table.row().space(20f)
        }

        if (showPlayerKnowledgeInfo) {
            table.add(
                createLabel(
                    "Player knowledge: ",
                    gdxSettings.normalFontSize
                )
            )

            table.row().space(10f)

            table.add(
                createKnowledgeDataTable(
                    playerData.playerInternalData.playerScienceData().playerKnowledgeData
                )
            )

            table.row().space(20f)
        }

        if (showScienceApplicationInfo) {
            table.add(
                createLabel(
                    "Science application: ",
                    gdxSettings.normalFontSize
                )
            )

            table.row().space(10f)

            table.add(
                createScienceApplicationDataTable(
                    playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                )
            )

            table.row().space(20f)
        }
    }

    private fun createInfoOptionTable(): Table {
        val nestedTable = Table()

        val commonSenseButton = createTextButton(
            "Common sense",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCommonSenseInfo = true
            showPlayerKnowledgeInfo = false
            showScienceApplicationInfo = false
            updateTable()
        }
        nestedTable.add(commonSenseButton).pad(10f)

        val playerKnowledgeButton = createTextButton(
            "Player knowledge",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCommonSenseInfo = false
            showPlayerKnowledgeInfo = true
            showScienceApplicationInfo = false
            updateTable()
        }
        nestedTable.add(playerKnowledgeButton).pad(10f)

        val scienceApplicationButton = createTextButton(
            "Science application",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCommonSenseInfo = false
            showPlayerKnowledgeInfo = false
            showScienceApplicationInfo = true
            updateTable()
        }
        nestedTable.add(scienceApplicationButton).pad(10f)

        return nestedTable
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

    private fun createScienceApplicationDataTable(
        scienceApplicationData: ScienceApplicationData
    ): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Ideal spaceship",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Core rest mass: ${scienceApplicationData.idealSpaceship.coreRestMass}",
                gdxSettings.smallFontSize
            )
        )


        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. movement fuel delta: ${scienceApplicationData.idealSpaceship.maxMovementDeltaFuelRestMass}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Ideal population: ${scienceApplicationData.idealSpaceship.idealPopulation}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Ideal fuel factory",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. output: ${scienceApplicationData.idealFuelFactory.maxOutputAmount}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ${scienceApplicationData.idealFuelFactory.maxNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Ideal resource factory: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createIdealResourceFactoryMapTable())

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Other production",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Ideal entertainment quality: ${scienceApplicationData.idealEntertainmentQuality.quality1}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Logistic",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Logistic fuel loss: ${scienceApplicationData.fuelLogisticsLossFractionPerDistance}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Logistic resource loss: ${scienceApplicationData.resourceLogisticsLossFractionPerDistance}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Military",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Military base attack factor: ${scienceApplicationData.militaryBaseAttackFactor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Military base shield factor: ${scienceApplicationData.militaryBaseShieldFactor}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createIdealResourceFactoryMapTable(): Table {
        val nestedTable = Table()

        val container = Container(
            createIdealResourceFactoryTable(
                ResourceType.PLANT,
                playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            )
        )

        nestedTable.add(
            createLabel(
                "Resource: ",
                gdxSettings.smallFontSize
            )
        )

        // Entertainment resource is not produced by factory
        val idealResourceFactorySelectBox = createSelectBox(
            ResourceType.values().toList() - ResourceType.ENTERTAINMENT,
            ResourceType.values().first(),
            gdxSettings.smallFontSize
        ) { resourceType, _ ->
            container.actor = createIdealResourceFactoryTable(
                resourceType,
                playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            )
        }
        nestedTable.add(idealResourceFactorySelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(container).colspan(2)

        return nestedTable
    }

    private fun createIdealResourceFactoryTable(
        resourceType: ResourceType,
        scienceApplicationData: ScienceApplicationData,
    ): Table {
        val nestedTable = Table()

        val idealFactory: ResourceFactoryInternalData =
            scienceApplicationData.getIdealResourceFactory(resourceType)

        nestedTable.add(
            createLabel(
                "Output resource: ${idealFactory.outputResource}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Output quality: ${idealFactory.maxOutputResourceQualityData.quality1}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Output amount: ${idealFactory.maxOutputAmount}",
                gdxSettings.smallFontSize
            )
        )

        idealFactory.inputResourceMap.forEach { (resourceType, inputResourceData) ->
            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Input $resourceType quality: ${inputResourceData.qualityData.quality1}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Input $resourceType amount: ${inputResourceData.amount}",
                    gdxSettings.smallFontSize
                )
            )
        }

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Fuel consumption rate: ${idealFactory.fuelRestMassConsumptionRate}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Employee: ${idealFactory.maxNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }
}