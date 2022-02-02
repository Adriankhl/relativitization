package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.*
import relativitization.universe.data.components.defaults.physics.FuelRestMassTargetProportionData
import relativitization.universe.maths.number.Notation

class EconomyInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    private var selectedResourceType: ResourceType = ResourceType.PLANT

    private var selectedResourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST

    // Show one of the three data
    private var showFuelData: Boolean = true
    private var showResourceData: Boolean = false
    private var showTaxData: Boolean = false

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
                "Economy: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

        table.add(createInfoOptionTable())

        table.row().space(20f)

        if (showFuelData) {
            table.add(createFuelRestMassTable())

            table.row().space(30f)
        }

        if (showResourceData) {
            table.add(createResourceTable())

            table.row().space(30f)
        }

        if (showTaxData) {
            table.add(createTaxTable())

            table.row().space(30f)
        }

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())
    }

    private fun createInfoOptionTable(): Table {
        val nestedTable = Table()

        val fuelButton = createTextButton(
            "Fuel info",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showFuelData = true
            showResourceData = false
            showTaxData = false
            updateTable()
        }
        nestedTable.add(fuelButton).pad(10f)

        val resourceButton = createTextButton(
            "Resource info",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showFuelData = false
            showResourceData = true
            showTaxData = false
            updateTable()
        }
        nestedTable.add(resourceButton).pad(10f)

        val taxButton = createTextButton(
            "Tax info",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showFuelData = false
            showResourceData = false
            showTaxData = true
            updateTable()
        }
        nestedTable.add(taxButton).pad(10f)

        return nestedTable
    }

    private fun createFuelRestMassTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Fuel",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Storage: ${playerData.playerInternalData.physicsData().fuelRestMassData.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Movement: ${playerData.playerInternalData.physicsData().fuelRestMassData.movement}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Production: ${playerData.playerInternalData.physicsData().fuelRestMassData.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Trade: ${playerData.playerInternalData.physicsData().fuelRestMassData.trade}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(30f)

        if (playerData.playerId == game.universeClient.getUniverseData3D().id) {
            val targetStorageSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = playerData.playerInternalData.physicsData().fuelRestMassTargetProportionData.storage
                    .toFloat(),
            )

            val targetMovementSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = playerData.playerInternalData.physicsData().fuelRestMassTargetProportionData.movement
                    .toFloat(),
            )

            val targetProductionSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = playerData.playerInternalData.physicsData().fuelRestMassTargetProportionData.production
                    .toFloat(),
            )

            val targetTradeSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = playerData.playerInternalData.physicsData().fuelRestMassTargetProportionData.trade
                    .toFloat(),
            )

            val changeFuelRestMassTargetProportionButton = createTextButton(
                "Change fuel proportion",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                extraColor = commandButtonColor,
            ) {
                val changeFuelRestMassTargetProportionCommand = ChangeFuelRestMassTargetProportionCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    fuelRestMassTargetProportionData = FuelRestMassTargetProportionData(
                        storage = targetStorageSlider.value.toDouble(),
                        movement = targetMovementSlider.value.toDouble(),
                        production = targetProductionSlider.value.toDouble(),
                        trade = targetTradeSlider.value.toDouble(),
                    ),
                )

                game.universeClient.currentCommand = changeFuelRestMassTargetProportionCommand
            }
            nestedTable.add(changeFuelRestMassTargetProportionButton).colspan(2)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Storage: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetStorageSlider)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Movement: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetMovementSlider)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Production: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetProductionSlider)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Trade: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetTradeSlider)

            nestedTable.row().space(30f)
        }


        val sendFuelSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val sendFuelButton = createTextButton(
            "Send fuel to this player",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val sendFuelFromStorageCommand = SendFuelFromStorageCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                amount = game.universeClient.getCurrentPlayerData().playerInternalData.physicsData()
                    .fuelRestMassData.storage * sendFuelSlider.value,
                senderFuelLossFractionPerDistance = game.universeClient.getCurrentPlayerData()
                    .playerInternalData.playerScienceData()
                    .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance
            )

            game.universeClient.currentCommand = sendFuelFromStorageCommand
        }
        nestedTable.add(sendFuelButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(sendFuelSlider).colspan(2)

        return nestedTable
    }

    private fun createResourceTable(): Table {
        val nestedTable = Table()

        val singleResourceData: SingleResourceData = playerData.playerInternalData.economyData()
            .resourceData.getSingleResourceData(selectedResourceType, selectedResourceQualityClass)

        nestedTable.add(
            createLabel(
                "Resource: ",
                gdxSettings.smallFontSize
            )
        )

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            selectedResourceType,
            gdxSettings.smallFontSize
        ) { resourceType, _ ->
            selectedResourceType = resourceType
            updateTable()
        }
        nestedTable.add(resourceTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Quality class: ",
                gdxSettings.smallFontSize
            )
        )

        val resourceQualityClassSelectBox = createSelectBox(
            ResourceQualityClass.values().toList(),
            selectedResourceQualityClass,
            gdxSettings.smallFontSize
        ) { resourceQualityClass, _ ->
            selectedResourceQualityClass = resourceQualityClass
            updateTable()
        }
        nestedTable.add(resourceQualityClassSelectBox)

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Price: ${singleResourceData.resourcePrice}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Quality: ${singleResourceData.resourceQuality.quality1}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Quality lower bound: ${singleResourceData.resourceQualityLowerBound.quality1}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Storage: ${singleResourceData.resourceAmount.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Production: ${singleResourceData.resourceAmount.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "Trade: ${singleResourceData.resourceAmount.trade}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(30f)

        if (playerData.playerId == game.universeClient.getUniverseData3D().id) {
            val targetStorageSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = singleResourceData.resourceTargetProportion.storage.toFloat(),
            )

            val targetProductionSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = singleResourceData.resourceTargetProportion.production.toFloat(),
            )

            val targetTradeSlider = createSlider(
                min = 0f,
                max = 1f,
                stepSize = 0.01f,
                default = singleResourceData.resourceTargetProportion.trade.toFloat(),
            )


            val changeFuelRestMassTargetProportionButton = createTextButton(
                "Change fuel proportion",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                extraColor = commandButtonColor,
            ) {
                val changeResourceTargetProportionCommand = ChangeResourceTargetProportionCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    resourceType = selectedResourceType,
                    resourceQualityClass = selectedResourceQualityClass,
                    resourceTargetProportionData = ResourceTargetProportionData(
                        storage = targetStorageSlider.value.toDouble(),
                        production = targetProductionSlider.value.toDouble(),
                        trade = targetTradeSlider.value.toDouble(),
                    ),
                )

                game.universeClient.currentCommand = changeResourceTargetProportionCommand
            }
            nestedTable.add(changeFuelRestMassTargetProportionButton).colspan(2)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Storage: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetStorageSlider)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Production: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetProductionSlider)

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Trade: ",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.add(targetTradeSlider)

            nestedTable.row().space(30f)
        }

        val sendResourceSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val sendResourceButton = createTextButton(
            "Send resource to this player",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val sendResourceFromStorageCommand = SendResourceFromStorageCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                resourceQualityClass = selectedResourceQualityClass,
                resourceQualityData = singleResourceData.resourceQuality,
                amount = singleResourceData.resourceAmount.storage * sendResourceSlider.value,
                senderResourceLossFractionPerDistance = game.universeClient.getCurrentPlayerData()
                    .playerInternalData.playerScienceData()
                    .playerScienceApplicationData.resourceLogisticsLossFractionPerDistance,
            )

            game.universeClient.currentCommand = sendResourceFromStorageCommand
        }
        nestedTable.add(sendResourceButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(sendResourceSlider).colspan(2)

        return nestedTable
    }

    private fun createTaxTable(): Table {
        val nestedTable = Table()

        val taxRateData: TaxRateData = playerData.playerInternalData.economyData().taxData
            .taxRateData

        val importTariff: Double = taxRateData.importTariff.getResourceTariffRate(
            game.universeClient.getUniverseData3D().get(
                game.universeClient.newSelectedPlayerId
            ).topLeaderId(),
            selectedResourceType
        )

        val exportTariff: Double = taxRateData.exportTariff.getResourceTariffRate(
            game.universeClient.getUniverseData3D().get(
                game.universeClient.newSelectedPlayerId
            ).topLeaderId(),
            selectedResourceType
        )

        val newImportTariff = createDoubleTextField(
            importTariff,
            gdxSettings.smallFontSize
        )

        val newExportTariff = createDoubleTextField(
            exportTariff,
            gdxSettings.smallFontSize
        )

        val newLowIncomeTax = createDoubleTextField(
            taxRateData.incomeTax.lowIncomeTaxRate,
            gdxSettings.smallFontSize
        )

        val newMiddleIncomeTax = createDoubleTextField(
            taxRateData.incomeTax.middleIncomeTaxRate,
            gdxSettings.smallFontSize
        )

        val newHighIncomeTax = createDoubleTextField(
            taxRateData.incomeTax.highIncomeTaxRate,
            gdxSettings.smallFontSize
        )

        val newLowMiddleIncomeBoundary = createDoubleTextField(
            taxRateData.incomeTax.lowMiddleBoundary,
            gdxSettings.smallFontSize
        )

        val newMiddleHighIncomeBoundary = createDoubleTextField(
            taxRateData.incomeTax.middleHighBoundary,
            gdxSettings.smallFontSize
        )

        nestedTable.add(
            createLabel(
                "Tariffs",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Resource: ",
                gdxSettings.smallFontSize
            )
        )

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            selectedResourceType,
            gdxSettings.smallFontSize
        ) { resourceType, _ ->
            selectedResourceType = resourceType
            updateTable()
        }
        nestedTable.add(resourceTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Import tariff: $importTariff",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeImportTariffButton = createTextButton(
            "Change import tariff",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeDefaultImportTariffCommand = ChangeDefaultImportTariffCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                rate = newImportTariff.value,
            )

            game.universeClient.currentCommand = changeDefaultImportTariffCommand
        }
        nestedTable.add(changeImportTariffButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New import tariff: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newImportTariff.textField)

        nestedTable.row().space(10f)

        val newImportTariffSliderButtonTable = createDoubleSliderButtonTable(
            newImportTariff.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newImportTariff.value }
        ) {
            newImportTariff.value = it
        }
        nestedTable.add(newImportTariffSliderButtonTable).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Export tariff: $exportTariff",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeExportTariffButton = createTextButton(
            "Change export tariff",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeDefaultExportTariffCommand = ChangeDefaultExportTariffCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                rate = newExportTariff.value,
            )

            game.universeClient.currentCommand = changeDefaultExportTariffCommand
        }
        nestedTable.add(changeExportTariffButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New export tariff: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newExportTariff.textField)

        nestedTable.row().space(10f)

        val newExportTariffSliderButtonTable = createDoubleSliderButtonTable(
            newExportTariff.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newExportTariff.value }
        ) {
            newExportTariff.value = it
        }
        nestedTable.add(newExportTariffSliderButtonTable).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Low income tax: ${taxRateData.incomeTax.lowIncomeTaxRate}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeLowIncomeTaxButton = createTextButton(
            "Change low income tax",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeLowIncomeTaxCommand = ChangeLowIncomeTaxCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                rate = newLowIncomeTax.value,
            )

            game.universeClient.currentCommand = changeLowIncomeTaxCommand
        }
        nestedTable.add(changeLowIncomeTaxButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New low income tax: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newLowIncomeTax.textField)

        nestedTable.row().space(10f)

        val changeLowIncomeTaxSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        ) { fl, _ ->
            newLowIncomeTax.value = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(changeLowIncomeTaxSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Middle income tax: ${taxRateData.incomeTax.middleIncomeTaxRate}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeMiddleIncomeTaxButton = createTextButton(
            "Change middle income tax",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeMiddleIncomeTaxCommand = ChangeMiddleIncomeTaxCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                rate = newMiddleIncomeTax.value,
            )

            game.universeClient.currentCommand = changeMiddleIncomeTaxCommand
        }
        nestedTable.add(changeMiddleIncomeTaxButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New middle income tax: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newMiddleIncomeTax.textField)

        nestedTable.row().space(10f)

        val changeMiddleIncomeTaxSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        ) { fl, _ ->
            newMiddleIncomeTax.value = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(changeMiddleIncomeTaxSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "High income tax: ${taxRateData.incomeTax.highIncomeTaxRate}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeHighIncomeTaxButton = createTextButton(
            "Change high income tax",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeHighIncomeTaxCommand = ChangeHighIncomeTaxCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                rate = newHighIncomeTax.value,
            )

            game.universeClient.currentCommand = changeHighIncomeTaxCommand
        }
        nestedTable.add(changeHighIncomeTaxButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New high income tax: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newHighIncomeTax.textField)

        nestedTable.row().space(10f)

        val changeHighIncomeTaxSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        ) { fl, _ ->
            newHighIncomeTax.value = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(changeHighIncomeTaxSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Low-middle boundary: ${taxRateData.incomeTax.lowMiddleBoundary}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeLowMiddleBoundaryButton = createTextButton(
            "Change low-middle boundary",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeLowMiddleBoundaryCommand = ChangeLowMiddleBoundaryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                boundary = newLowMiddleIncomeBoundary.value,
            )

            game.universeClient.currentCommand = changeLowMiddleBoundaryCommand
        }
        nestedTable.add(changeLowMiddleBoundaryButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New low-middle boundary: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newLowMiddleIncomeBoundary.textField)

        nestedTable.row().space(10f)

        val changeLowMiddleBoundarySliderButtonTable = createDoubleSliderButtonTable(
            newLowMiddleIncomeBoundary.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newLowMiddleIncomeBoundary.value }
        ) {
            newLowMiddleIncomeBoundary.value = it
        }
        nestedTable.add(changeLowMiddleBoundarySliderButtonTable).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Middle-high boundary: ${taxRateData.incomeTax.middleHighBoundary}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeMiddleHighBoundaryButton = createTextButton(
            "Change middle-high boundary",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeMiddleHighBoundaryCommand = ChangeMiddleHighBoundaryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                boundary = newMiddleHighIncomeBoundary.value,
            )

            game.universeClient.currentCommand = changeMiddleHighBoundaryCommand
        }
        nestedTable.add(changeMiddleHighBoundaryButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New middle-high boundary: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newMiddleHighIncomeBoundary.textField)

        nestedTable.row().space(10f)

        val changeMiddleHighBoundarySliderButtonTable = createDoubleSliderButtonTable(
            newMiddleHighIncomeBoundary.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newMiddleHighIncomeBoundary.value }
        ) {
            newMiddleHighIncomeBoundary.value = it
        }
        nestedTable.add(changeMiddleHighBoundarySliderButtonTable).colspan(2)

        return nestedTable
    }
}