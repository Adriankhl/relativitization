package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.BuildForeignFuelFactoryCommand
import relativitization.universe.data.commands.ChangeSalaryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.ResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.maths.number.Notation
import relativitization.universe.utils.RelativitizationLogManager

class PopSystemInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // The id of the carrier to show in the info
    private var carrierId: Int = -1

    private var carrierTable: Table = Table()

    private var popType: PopType = PopType.LABOURER


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
        updateTable()
    }


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getCurrentPlayerData()
        }


        if (!playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)) {
            carrierId = playerData.playerInternalData
                .popSystemData().carrierDataMap.keys.firstOrNull() ?: -1
        }
    }


    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Pop system: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        )

        table.row().space(20f)

        table.add(createLabel("Carrier:", gdxSettings.normalFontSize))

        table.row()

        val carrierSelectBox = createSelectBox(
            playerData.playerInternalData.popSystemData().carrierDataMap.keys.toList(),
            carrierId,
            gdxSettings.smallFontSize,
        ) { id, _ ->
            carrierId = id
            updateCarrierTable()
        }
        table.add(carrierSelectBox)

        table.row().space(20f)

        if (playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)) {
            updateCarrierTable()
        }
        table.add(carrierTable)
    }

    private fun updateCarrierTable() {
        carrierTable.clear()

        val carrier: CarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        carrierTable.add(
            createLabel(
                "Carrier type: ${carrier.carrierType}",
                gdxSettings.smallFontSize
            )
        )

        carrierTable.row().space(30f)

        carrierTable.add(createCarrierInternalDataTable(carrier.carrierInternalData))

        carrierTable.row().space(30f)

        carrierTable.add(createLabel("Pop:", gdxSettings.normalFontSize))

        carrierTable.row()

        val popTypeSelectBox = createSelectBox(
            PopType.values().toList(),
            popType,
            gdxSettings.smallFontSize,
        ) { type, _ ->
            popType = type
            updateCarrierTable()
        }
        carrierTable.add(popTypeSelectBox)

        carrierTable.row().space(30f)

        carrierTable.add(createPopTable(carrier.allPopData))

        carrierTable.row()

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        carrierTable.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())
    }

    private fun createCarrierInternalDataTable(carrierInternalData: CarrierInternalData): Table {
        val nestedTable = Table()

        val internalDataLabel = createLabel(
            "Carrier internal data:",
            gdxSettings.normalFontSize
        )
        nestedTable.add(internalDataLabel)

        nestedTable.row().space(10f)

        val coreMassLabel = createLabel(
            "Core rest mass: ${carrierInternalData.coreRestMass}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(coreMassLabel)

        nestedTable.row().space(10f)

        val moveMaxPowerLabel = createLabel(
            "Max. movement fuel delta: ${carrierInternalData.maxMovementDeltaFuelRestMass}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(moveMaxPowerLabel)

        nestedTable.row().space(10f)

        val idealPopulationLabel = createLabel(
            "Ideal population: ${carrierInternalData.idealPopulation}",
            gdxSettings.smallFontSize
        )
        nestedTable.add(idealPopulationLabel)

        return nestedTable
    }

    private fun createPopTable(allPopType: AllPopData): Table {
        val nestedTable = Table()

        nestedTable.add(createCommonPopTable(allPopType.getCommonPopData(popType)))

        nestedTable.row().space(30f)

        when (popType) {
            PopType.LABOURER -> nestedTable.add(createLabourerTable(allPopType.labourerPopData))
            PopType.ENGINEER -> nestedTable.add(Table())
            PopType.SCHOLAR -> nestedTable.add(Table())
            PopType.EDUCATOR -> nestedTable.add(Table())
            PopType.MEDIC -> nestedTable.add(Table())
            PopType.SERVICE_WORKER -> nestedTable.add(Table())
            PopType.ENTERTAINER -> nestedTable.add(Table())
            PopType.SOLDIER -> nestedTable.add(Table())
        }


        return nestedTable
    }

    private fun createCommonPopTable(commonPopData: CommonPopData): Table {
        val nestedTable = Table()

        nestedTable.add(createLabel("Common pop data: ", gdxSettings.normalFontSize))

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Adult population: ${commonPopData.adultPopulation}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Education level: ${commonPopData.educationLevel}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Unemployment rate: ${commonPopData.unemploymentRate}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Satisfaction: ${commonPopData.satisfaction}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Saving: ${commonPopData.saving}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Salary: ${commonPopData.salary}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createTargetSalaryTable(commonPopData.salary))

        nestedTable.row().space(10f)

        nestedTable.add(createLabel("Pop desire: ", gdxSettings.smallFontSize))

        nestedTable.row().space(10f)

        commonPopData.desireResourceMap.forEach { (resourceType, desireData) ->
            nestedTable.add(
                createPopDesireTable(
                    resourceType,
                    desireData
                )
            )

            nestedTable.row().space(10f)
        }

        return nestedTable
    }

    private fun createTargetSalaryTable(defaultSalary: Double): Table {
        val nestedTable = Table()

        val targetSalary = createDoubleTextField(
            default = defaultSalary,
            fontSize = gdxSettings.smallFontSize
        )


        val changeSalaryTextButton = createTextButton(
            text = "Change salary",
            fontSize = gdxSettings.smallFontSize,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            val changeSalaryCommand = ChangeSalaryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierId = carrierId,
                popType = popType,
                salary = targetSalary.num,
            )

            game.universeClient.currentCommand = changeSalaryCommand
        }
        nestedTable.add(changeSalaryTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Target salary: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(targetSalary.textField)

        nestedTable.row().space(10f)

        val targetSalarySliderButtonTable = createDoubleSliderButtonTable(
            default = targetSalary.num,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { targetSalary.num }
        ) {
            targetSalary.num = it
        }
        nestedTable.add(targetSalarySliderButtonTable).colspan(2)

        return nestedTable
    }

    private fun createPopDesireTable(
        resourceType: ResourceType,
        resourceDesireData: ResourceDesireData
    ): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Resource: $resourceType",
                gdxSettings.smallFontSize
            ),
        )

        nestedTable.row()

        nestedTable.add(
            createLabel(
                "Amount: ${resourceDesireData.desireAmount}",
                gdxSettings.smallFontSize
            ),
        )

        nestedTable.row()
        nestedTable.add(
            createLabel(
                "Quality: ${resourceDesireData.desireQuality.quality1}",
                gdxSettings.smallFontSize
            ),
        )


        return nestedTable
    }

    private fun createLabourerTable(labourerPopData: LabourerPopData): Table {
        val nestedTable = Table()

        val ownerId = createIntTextField(
            playerData.playerId,
            gdxSettings.smallFontSize
        )

        val qualityLevel = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        val storedFuelRestMass = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val numBuilding = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        nestedTable.add(
            createLabel(
                "Labourer data:",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(20f)

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildForeignFuelFactoryCommand = BuildForeignFuelFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                senderTopLeaderId = game.universeClient.getCurrentPlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = ownerId.num,
                fuelFactoryInternalData = game.universeClient.getCurrentPlayerData()
                    .playerInternalData.playerScienceData()
                    .playerScienceApplicationData.newFuelFactoryInternalData(qualityLevel.num),
                qualityLevel = qualityLevel.num,
                storedFuelRestMass = storedFuelRestMass.num,
                numBuilding = numBuilding.num
            )

            game.universeClient.currentCommand = buildForeignFuelFactoryCommand
        }
        nestedTable.add(buildForeignFuelFactoryTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New factory owner: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(ownerId.textField)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New factory quality level: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(qualityLevel.textField)

        nestedTable.row().space(10f)

        val qualityLevelSlider = createSlider(
            0f,
            1f,
            0.01f,
            1f
        ) { fl, _ ->
            qualityLevel.num = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(qualityLevelSlider).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New factory stored fuel: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(storedFuelRestMass.textField)

        nestedTable.row().space(10f)

        val storedFuelRestMassDoubleSliderButton = createDoubleSliderButtonTable(
            default = storedFuelRestMass.num,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { storedFuelRestMass.num },
        ) {
            storedFuelRestMass.num = it
        }

        nestedTable.add(storedFuelRestMassDoubleSliderButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New factory num building: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(numBuilding.textField)

        nestedTable.row().space(10f)

        val numBuildingDoubleSliderButton = createDoubleSliderButtonTable(
            default = numBuilding.num,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { numBuilding.num },
        ) {
            numBuilding.num = it
        }

        nestedTable.add(numBuildingDoubleSliderButton).colspan(2)


        return nestedTable
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}