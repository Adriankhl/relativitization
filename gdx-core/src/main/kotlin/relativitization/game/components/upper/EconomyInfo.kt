package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.economy.SingleResourceData
import relativitization.universe.data.components.defaults.economy.TaxRateData
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

        val newTargetFuelStorage = createDoubleTextField(
            playerData.playerInternalData.physicsData().targetFuelRestMassData.storage,
            fontSize = gdxSettings.smallFontSize,
        )

        val newTargetFuelMovement = createDoubleTextField(
            playerData.playerInternalData.physicsData().targetFuelRestMassData.movement,
            fontSize = gdxSettings.smallFontSize,
        )

        val newTargetFuelProduction = createDoubleTextField(
            playerData.playerInternalData.physicsData().targetFuelRestMassData.production,
            fontSize = gdxSettings.smallFontSize,
        )

        val sendFuelSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val sendFuelButton = createTextButton(
            "Send fuel to this player",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
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

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Fuel rest mass data: ",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Storage: ${playerData.playerInternalData.physicsData().fuelRestMassData.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Target storage: ${playerData.playerInternalData.physicsData().targetFuelRestMassData.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeStorageFuelButton = createTextButton(
            "Change target storage",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val changeStorageFuelTargetCommand = ChangeStorageFuelTargetCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetAmount = newTargetFuelStorage.value,
            )

            game.universeClient.currentCommand = changeStorageFuelTargetCommand
        }
        nestedTable.add(changeStorageFuelButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New target storage: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newTargetFuelStorage.textField)

        nestedTable.row().space(10f)

        val newTargetFuelStorageButtonSlider = createDoubleSliderButtonTable(
            newTargetFuelStorage.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newTargetFuelStorage.value }
        ) {
            newTargetFuelStorage.value = it
        }
        nestedTable.add(newTargetFuelStorageButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Movement: ${playerData.playerInternalData.physicsData().fuelRestMassData.movement}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToMovementSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToMovementButton = createTextButton(
            "Transfer to movement",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToMovementCommand = TransferFuelToMovementCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToMovementSlider.value
            )

            game.universeClient.currentCommand = transferFuelToMovementCommand
        }
        nestedTable.add(transferToMovementButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToMovementSlider).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Target movement: ${playerData.playerInternalData.physicsData().targetFuelRestMassData.movement}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeMovementFuelButton = createTextButton(
            "Change target movement",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val changeMovementFuelTargetCommand = ChangeMovementFuelTargetCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetAmount = newTargetFuelMovement.value,
            )

            game.universeClient.currentCommand = changeMovementFuelTargetCommand
        }
        nestedTable.add(changeMovementFuelButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New target movement: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newTargetFuelMovement.textField)

        nestedTable.row().space(10f)

        val newTargetFuelMovementButtonSlider = createDoubleSliderButtonTable(
            newTargetFuelMovement.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newTargetFuelMovement.value }
        ) {
            newTargetFuelMovement.value = it
        }
        nestedTable.add(newTargetFuelMovementButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Production: ${playerData.playerInternalData.physicsData().fuelRestMassData.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToProductionSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToProductionButton = createTextButton(
            "Transfer to production",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToProductionCommand = TransferFuelToProductionCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToProductionSlider.value
            )

            game.universeClient.currentCommand = transferFuelToProductionCommand
        }
        nestedTable.add(transferToProductionButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToProductionSlider).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Target production: ${playerData.playerInternalData.physicsData().targetFuelRestMassData.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeProductionFuelButton = createTextButton(
            "Change target production",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val changeProductionFuelTargetCommand = ChangeProductionFuelTargetCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetAmount = newTargetFuelProduction.value,
            )

            game.universeClient.currentCommand = changeProductionFuelTargetCommand
        }
        nestedTable.add(changeProductionFuelButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New target production: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newTargetFuelProduction.textField)

        nestedTable.row().space(10f)

        val newTargetFuelProductionButtonSlider = createDoubleSliderButtonTable(
            newTargetFuelProduction.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newTargetFuelProduction.value }
        ) {
            newTargetFuelProduction.value = it
        }
        nestedTable.add(newTargetFuelProductionButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Trade: ${playerData.playerInternalData.physicsData().fuelRestMassData.trade}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToTradeSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToTradeButton = createTextButton(
            "Transfer to trade",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferFuelToTradeCommand = TransferFuelToTradeCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                amount = playerData.playerInternalData.physicsData().fuelRestMassData.storage * transferToTradeSlider.value
            )

            game.universeClient.currentCommand = transferFuelToTradeCommand
        }
        nestedTable.add(transferToTradeButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToTradeSlider).colspan(2)

        return nestedTable
    }

    private fun createResourceTable(): Table {
        val nestedTable = Table()

        val singleResourceData: SingleResourceData = playerData.playerInternalData.economyData()
            .resourceData.getSingleResourceData(selectedResourceType, selectedResourceQualityClass)

        val newTargetResourceStorage = createDoubleTextField(
            singleResourceData.resourceTargetAmount.storage,
            gdxSettings.smallFontSize
        )

        val newTargetResourceProduction = createDoubleTextField(
            singleResourceData.resourceTargetAmount.production,
            gdxSettings.smallFontSize
        )

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
                "Target storage: ${singleResourceData.resourceTargetAmount.storage}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val changeStorageResourceButton = createTextButton(
            "Change target storage",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val changeStorageResourceTargetCommand = ChangeStorageResourceTargetCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                resourceQualityClass = selectedResourceQualityClass,
                targetAmount = newTargetResourceStorage.value,
            )

            game.universeClient.currentCommand = changeStorageResourceTargetCommand
        }
        nestedTable.add(changeStorageResourceButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New target storage: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newTargetResourceStorage.textField)

        nestedTable.row().space(10f)

        val newTargetResourceStorageButtonSlider = createDoubleSliderButtonTable(
            newTargetResourceStorage.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newTargetResourceStorage.value }
        ) {
            newTargetResourceStorage.value = it
        }
        nestedTable.add(newTargetResourceStorageButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Production: ${singleResourceData.resourceAmount.production}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToProductionSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToProductionButton = createTextButton(
            "Transfer to production",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferResourceToProductionCommand = TransferResourceToProductionCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                resourceQualityClass = selectedResourceQualityClass,
                amount = singleResourceData.resourceAmount.storage * transferToProductionSlider.value,
            )

            game.universeClient.currentCommand = transferResourceToProductionCommand
        }
        nestedTable.add(transferToProductionButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToProductionSlider).colspan(2)

        nestedTable.row().space(10f)

        val changeProductionResourceButton = createTextButton(
            "Change target production",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val changeProductionResourceTargetCommand = ChangeProductionResourceTargetCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                resourceQualityClass = selectedResourceQualityClass,
                targetAmount = newTargetResourceProduction.value,
            )

            game.universeClient.currentCommand = changeProductionResourceTargetCommand
        }
        nestedTable.add(changeProductionResourceButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New target production: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newTargetResourceProduction.textField)

        nestedTable.row().space(10f)

        val newTargetResourceProductionButtonSlider = createDoubleSliderButtonTable(
            newTargetResourceProduction.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newTargetResourceProduction.value }
        ) {
            newTargetResourceProduction.value = it
        }
        nestedTable.add(newTargetResourceProductionButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Trade: ${singleResourceData.resourceAmount.trade}",
                gdxSettings.smallFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val transferToTradeSlider = createSlider(
            min = 0f,
            max = 1f,
            stepSize = 0.01f,
            default = 0f,
        )

        val transferToTradeButton = createTextButton(
            "Transfer to trade",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val transferResourceToTradeCommand = TransferResourceToTradeCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                resourceType = selectedResourceType,
                resourceQualityClass = selectedResourceQualityClass,
                amount = singleResourceData.resourceAmount.storage * transferToTradeSlider.value,
            )

            game.universeClient.currentCommand = transferResourceToTradeCommand
        }
        nestedTable.add(transferToTradeButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(transferToTradeSlider).colspan(2)

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
            gdxSettings.soundEffectsVolume
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

        val newImportTariffButtonSlider = createDoubleSliderButtonTable(
            newImportTariff.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newImportTariff.value }
        ) {
            newImportTariff.value = it
        }
        nestedTable.add(newImportTariffButtonSlider).colspan(2)

        nestedTable.row().spaceTop(30f)

        nestedTable.add(
            createLabel(
                "Export tariff: $exportTariff",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val changeExportTariffButton = createTextButton(
            "Change export tariff",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
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

        val newExportTariffButtonSlider = createDoubleSliderButtonTable(
            newExportTariff.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newExportTariff.value }
        ) {
            newExportTariff.value = it
        }
        nestedTable.add(newExportTariffButtonSlider).colspan(2)

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
            gdxSettings.soundEffectsVolume
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
            gdxSettings.soundEffectsVolume
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
            gdxSettings.soundEffectsVolume
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
            gdxSettings.soundEffectsVolume
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

        val changeLowMiddleBoundarySliderButton = createDoubleSliderButtonTable(
            newLowMiddleIncomeBoundary.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newLowMiddleIncomeBoundary.value }
        ) {
            newLowMiddleIncomeBoundary.value = it
        }
        nestedTable.add(changeLowMiddleBoundarySliderButton).colspan(2)

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
            gdxSettings.soundEffectsVolume
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

        val changeMiddleHighBoundarySliderButton = createDoubleSliderButtonTable(
            newMiddleHighIncomeBoundary.value,
            0.01f,
            2,
            40f * gdxSettings.imageScale,
            gdxSettings.soundEffectsVolume,
            currentValue = { newMiddleHighIncomeBoundary.value }
        ) {
            newMiddleHighIncomeBoundary.value = it
        }
        nestedTable.add(changeMiddleHighBoundarySliderButton).colspan(2)

        return nestedTable
    }
}