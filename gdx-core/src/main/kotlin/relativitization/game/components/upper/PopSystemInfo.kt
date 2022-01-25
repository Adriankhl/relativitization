package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.ResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.EngineerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.FuelFactoryData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.ScholarPopData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.InstituteData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.InstituteInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.service.ServicePopData
import relativitization.universe.data.components.defaults.popsystem.pop.service.export.PlayerExportCenterData
import relativitization.universe.data.components.defaults.popsystem.pop.service.export.PlayerSingleExportData
import relativitization.universe.data.components.defaults.popsystem.pop.soldier.SoldierPopData
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

    private var fuelFactoryId: Int = -1

    private var resourceFactoryType: ResourceType = ResourceType.PLANT

    private var resourceFactoryId: Int = -1

    private var instituteId: Int = -1

    private var laboratoryId: Int = -1

    private var exportCenterOwnerId: Int = -1

    private var exportCenterTargetId: Int = -1

    private var exportCenterResourceType: ResourceType = ResourceType.PLANT

    private var exportCenterResourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST

    // Variable to determine which information is shown, only one should be true
    private var showCarrierInfo: Boolean = true
    private var showNewCarrierInfo: Boolean = false
    private var showNewPlayerInfo: Boolean = false


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

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onSelectedKnowledgeDouble2DChange() {
        updateCarrierTable()
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

        val carrierIdSelectBox = createSelectBox(
            playerData.playerInternalData.popSystemData().carrierDataMap.keys.toList(),
            carrierId,
            gdxSettings.smallFontSize,
        ) { id, _ ->
            carrierId = id
            updateCarrierTable()
        }
        table.add(carrierIdSelectBox)

        table.row().space(10f)

        table.add(createInfoOptionTable())

        table.row().space(20f)

        if (showCarrierInfo) {
            if (playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)) {
                updateCarrierTable()
            }
            table.add(carrierTable)

            table.row().space(30f)
        }

        if (showNewCarrierInfo) {
            table.add(createNewCarrierTable())

            table.row().space(30f)
        }

        if (showNewPlayerInfo) {
            table.add(createNewPlayerTable())

            table.row().space(30f)
        }

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())
    }

    private fun createInfoOptionTable(): Table {
        val nestedTable = Table()

        val showCarrierInfoButton = createTextButton(
            "Carrier",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCarrierInfo = true
            showNewCarrierInfo = false
            showNewPlayerInfo = false
            updateTable()
        }
        nestedTable.add(showCarrierInfoButton).pad(10f)

        val showNewCarrierInfoButton = createTextButton(
            "New carrier",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCarrierInfo = false
            showNewCarrierInfo = true
            showNewPlayerInfo = false
            updateTable()
        }
        nestedTable.add(showNewCarrierInfoButton).pad(10f)

        val showNewPlayerInfoButton = createTextButton(
            "New player",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            showCarrierInfo = false
            showNewCarrierInfo = false
            showNewPlayerInfo = true
            updateTable()
        }
        nestedTable.add(showNewPlayerInfoButton).pad(10f)

        return nestedTable
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
        ).colspan(2)

        carrierTable.row().space(10f)

        carrierTable.add(createCarrierInternalDataTable(carrier.carrierInternalData)).colspan(2)

        carrierTable.row().space(20f)

        carrierTable.add(createLabel("Pop:", gdxSettings.normalFontSize)).colspan(2)

        carrierTable.row()

        val popTypeSelectBox = createSelectBox(
            PopType.values().toList(),
            popType,
            gdxSettings.smallFontSize,
        ) { type, _ ->
            popType = type
            updateCarrierTable()
        }
        carrierTable.add(popTypeSelectBox).colspan(2)

        carrierTable.row().space(30f)

        carrierTable.add(createPopTable(carrier.allPopData)).colspan(2)
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

    private fun createPopTable(allPopData: AllPopData): Table {
        val nestedTable = Table()

        nestedTable.add(createCommonPopTable(allPopData.getCommonPopData(popType)))

        nestedTable.row().space(30f)

        when (popType) {
            PopType.LABOURER -> nestedTable.add(createLabourerTable(allPopData.labourerPopData))
            PopType.SCHOLAR -> nestedTable.add(createScholarTable(allPopData.scholarPopData))
            PopType.ENGINEER -> nestedTable.add(createEngineerTable(allPopData.engineerPopData))
            PopType.EDUCATOR -> nestedTable.add(Table())
            PopType.MEDIC -> nestedTable.add(Table())
            PopType.SERVICE_WORKER -> nestedTable.add(createServiceTable(allPopData.servicePopData))
            PopType.ENTERTAINER -> nestedTable.add(Table())
            PopType.SOLDIER -> nestedTable.add(createSoldierTable(allPopData.soldierPopData))
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
                "Salary per employee: ${commonPopData.salaryPerEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createTargetSalaryTable(commonPopData.salaryPerEmployee))

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Saving: ${commonPopData.saving}",
                gdxSettings.smallFontSize
            )
        )

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

        nestedTable.row().space(10f)

        nestedTable.add(createLabel("Last desire input: ", gdxSettings.smallFontSize))

        nestedTable.row().space(10f)

        commonPopData.lastResourceInputMap.forEach { (resourceType, desireData) ->
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
                salary = targetSalary.value,
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
            default = targetSalary.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { targetSalary.value }
        ) {
            targetSalary.value = it
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

        nestedTable.add(
            createLabel(
                "Labourer data: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Factories: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createFuelFactoryMapTable(labourerPopData))

        nestedTable.row().space(10f)

        nestedTable.add(createResourceFactoryMapTable(labourerPopData))

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Build factory commands: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createBuildForeignFuelFactoryTable())

        nestedTable.row().space(10f)

        nestedTable.add(createBuildForeignResourceFactoryTable())

        nestedTable.row().space(10f)

        nestedTable.add(createBuildLocalFuelFactoryTable())

        nestedTable.row().space(10f)

        nestedTable.add(createBuildLocalResourceFactoryTable())

        return nestedTable
    }

    private fun createFuelFactoryMapTable(labourerPopData: LabourerPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Fuel factory id: ",
                gdxSettings.smallFontSize
            )
        )

        val fuelFactorySelectBox = createSelectBox(
            labourerPopData.fuelFactoryMap.keys.toList(),
            fuelFactoryId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            fuelFactoryId = id
            updateCarrierTable()
        }
        nestedTable.add(fuelFactorySelectBox)

        nestedTable.row().space(10f)

        if (labourerPopData.fuelFactoryMap.containsKey(fuelFactorySelectBox.selected)) {
            val fuelFactory: FuelFactoryData = labourerPopData.fuelFactoryMap.getValue(
                fuelFactorySelectBox.selected
            )

            nestedTable.add(
                createFuelFactoryTable(
                    fuelFactorySelectBox.selected,
                    fuelFactory
                )
            ).colspan(2)
        }

        return nestedTable
    }

    private fun createFuelFactoryTable(
        fuelFactoryId: Int,
        fuelFactoryData: FuelFactoryData
    ): Table {
        val nestedTable = Table()

        // Depending on whether this is a foreign player or not, use remove foreign / local command
        if (playerData.playerId == game.universeClient.getCurrentPlayerData().playerId) {
            val removeLocalFuelFactoryTextButton = createTextButton(
                text = "Remove factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val removeLocalFuelFactoryCommand = RemoveLocalFuelFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetFuelFactoryId = fuelFactoryId
                )

                game.universeClient.currentCommand = removeLocalFuelFactoryCommand
            }
            nestedTable.add(removeLocalFuelFactoryTextButton)
        } else {
            val removeForeignFuelFactoryTextButton = createTextButton(
                text = "Remove factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val removeForeignFuelFactoryCommand = RemoveForeignFuelFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetFuelFactoryId = fuelFactoryId
                )

                game.universeClient.currentCommand = removeForeignFuelFactoryCommand
            }
            nestedTable.add(removeForeignFuelFactoryTextButton)
        }

        nestedTable.row().space(10f)

        // Depending on whether this factory is open or close, show close/open button
        if (fuelFactoryData.isOpened) {
            val closeLocalFuelFactoryTextButton = createTextButton(
                text = "Close factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val closeLocalFuelFactoryCommand = CloseLocalFuelFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetFuelFactoryId = fuelFactoryId
                )

                game.universeClient.currentCommand = closeLocalFuelFactoryCommand
            }
            nestedTable.add(closeLocalFuelFactoryTextButton)
        } else {
            val openLocalFuelFactoryTextButton = createTextButton(
                text = "Open factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val openLocalFuelFactoryCommand = OpenLocalFuelFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetFuelFactoryId = fuelFactoryId
                )

                game.universeClient.currentCommand = openLocalFuelFactoryCommand
            }
            nestedTable.add(openLocalFuelFactoryTextButton)
        }

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Open: ${fuelFactoryData.isOpened}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Owner: ${fuelFactoryData.ownerPlayerId}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Number of building: ${fuelFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. output: ${fuelFactoryData.fuelFactoryInternalData.maxOutputAmount * fuelFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last output: ${fuelFactoryData.lastOutputAmount}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ${fuelFactoryData.fuelFactoryInternalData.maxNumEmployee * fuelFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last employee: ${fuelFactoryData.lastNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Stored fuel: ${fuelFactoryData.storedFuelRestMass}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val supplyFuelAmount = createDoubleTextField(0.0, gdxSettings.smallFontSize)

        val supplyFuelButton = createTextButton(
            "Supply Fuel",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val supplyForeignFuelFactoryCommand = SupplyForeignFuelFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetCarrierId = carrierId,
                targetFuelFactoryId = fuelFactoryId,
                amount = supplyFuelAmount.value
            )
            game.universeClient.currentCommand = supplyForeignFuelFactoryCommand
        }

        nestedTable.add(supplyFuelButton)

        nestedTable.row().space(10f)

        val supplyFuelTextTable = Table()
        supplyFuelTextTable.add(
            createLabel(
                "Supply fuel: ",
                gdxSettings.smallFontSize
            )
        )
        supplyFuelTextTable.add(supplyFuelAmount.textField)

        nestedTable.add(supplyFuelTextTable)

        nestedTable.row().space(10f)

        val supplyFuelSliderButton = createDoubleSliderButtonTable(
            default = supplyFuelAmount.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { supplyFuelAmount.value },
        ) {
            supplyFuelAmount.value = it
        }

        nestedTable.add(supplyFuelSliderButton)

        return nestedTable
    }

    private fun createResourceFactoryMapTable(labourerPopData: LabourerPopData): Table {
        val nestedTable = Table()

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            resourceFactoryType,
            gdxSettings.smallFontSize,
        ) { type, _ ->
            resourceFactoryType = type
            updateCarrierTable()
        }

        nestedTable.add(
            createLabel(
                "Resource: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(resourceTypeSelectBox)

        nestedTable.row().space(10f)

        val resourceFactorySelectBox = createSelectBox(
            labourerPopData.resourceFactoryMap.filter {
                it.value.resourceFactoryInternalData.outputResource == resourceTypeSelectBox.selected
            }.keys.toList(),
            resourceFactoryId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            resourceFactoryId = id
            updateCarrierTable()
        }

        nestedTable.add(
            createLabel(
                "Resource factory id: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(resourceFactorySelectBox)

        nestedTable.row().space(10f)

        if (labourerPopData.resourceFactoryMap.containsKey(resourceFactorySelectBox.selected)) {
            val resourceFactory: ResourceFactoryData = labourerPopData.resourceFactoryMap.getValue(
                resourceFactorySelectBox.selected
            )

            nestedTable.add(
                createResourceFactoryTable(
                    resourceFactorySelectBox.selected,
                    resourceFactory
                )
            ).colspan(2)
        }

        return nestedTable
    }

    private fun createResourceFactoryTable(
        resourceFactoryId: Int,
        resourceFactoryData: ResourceFactoryData
    ): Table {
        val nestedTable = Table()

        // Depending on whether this is a foreign player or not, use remove foreign / local command
        if (playerData.playerId == game.universeClient.getCurrentPlayerData().playerId) {
            val removeLocalResourceFactoryTextButton = createTextButton(
                text = "Remove factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val removeLocalResourceFactoryCommand = RemoveLocalResourceFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetResourceFactoryId = resourceFactoryId,
                )

                game.universeClient.currentCommand = removeLocalResourceFactoryCommand
            }
            nestedTable.add(removeLocalResourceFactoryTextButton)
        } else {
            val removeForeignResourceFactoryTextButton = createTextButton(
                text = "Remove factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val removeForeignResourceFactoryCommand = RemoveForeignResourceFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetResourceFactoryId = resourceFactoryId
                )
                game.universeClient.currentCommand = removeForeignResourceFactoryCommand
            }

            nestedTable.add(removeForeignResourceFactoryTextButton)
        }

        nestedTable.row().space(10f)

        if (resourceFactoryData.isOpened) {
            val closeLocalResourceFactoryTextButton = createTextButton(
                text = "Close factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val closeLocalResourceFactoryCommand = CloseLocalResourceFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetResourceFactoryId = resourceFactoryId,
                )

                game.universeClient.currentCommand = closeLocalResourceFactoryCommand
            }
            nestedTable.add(closeLocalResourceFactoryTextButton)
        } else {
            val openLocalResourceFactoryTextButton = createTextButton(
                text = "Open factory",
                fontSize = gdxSettings.smallFontSize,
                soundVolume = gdxSettings.soundEffectsVolume,
            ) {
                val openLocalResourceFactoryCommand = OpenLocalResourceFactoryCommand(
                    toId = playerData.playerId,
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                    targetCarrierId = carrierId,
                    targetResourceFactoryId = resourceFactoryId,
                )

                game.universeClient.currentCommand = openLocalResourceFactoryCommand
            }
            nestedTable.add(openLocalResourceFactoryTextButton)
        }

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Open: ${resourceFactoryData.isOpened}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Owner: ${resourceFactoryData.ownerPlayerId}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Number of building: ${resourceFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. quality: ${resourceFactoryData.resourceFactoryInternalData.maxOutputResourceQualityData.quality1}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last quality: ${resourceFactoryData.lastOutputQuality.quality1}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. output: ${resourceFactoryData.resourceFactoryInternalData.maxOutputAmount * resourceFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last output: ${resourceFactoryData.lastOutputAmount}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        resourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (resourceType, inputResource) ->
            val lastInputResource: InputResourceData = resourceFactoryData.lastInputResourceMap
                .getOrDefault(resourceType, InputResourceData())

            nestedTable.add(
                createLabel(
                    "Max. $resourceType input quality: ${inputResource.qualityData.quality1}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Last $resourceType input quality: ${lastInputResource.qualityData.quality1}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Max. $resourceType input: ${inputResource.amount * resourceFactoryData.numBuilding}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Last $resourceType input: ${lastInputResource.amount}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)
        }


        nestedTable.add(
            createLabel(
                "Max. employee: ${resourceFactoryData.resourceFactoryInternalData.maxNumEmployee * resourceFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last employee: ${resourceFactoryData.lastNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. fuel consumption: ${resourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRate * resourceFactoryData.numBuilding}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Stored fuel: ${resourceFactoryData.storedFuelRestMass}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val supplyFuelAmount = createDoubleTextField(0.0, gdxSettings.smallFontSize)

        val supplyFuelButton = createTextButton(
            "Supply Fuel",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val supplyForeignResourceFactoryCommand = SupplyForeignResourceFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetCarrierId = carrierId,
                targetResourceFactoryId = resourceFactoryId,
                amount = supplyFuelAmount.value
            )
            game.universeClient.currentCommand = supplyForeignResourceFactoryCommand
        }

        nestedTable.add(supplyFuelButton)

        nestedTable.row().space(10f)

        val supplyFuelTextTable = Table()
        supplyFuelTextTable.add(
            createLabel(
                "Supply fuel: ",
                gdxSettings.smallFontSize
            )
        )
        supplyFuelTextTable.add(supplyFuelAmount.textField)

        nestedTable.add(supplyFuelTextTable)

        nestedTable.row().space(10f)

        val supplyFuelSliderButton = createDoubleSliderButtonTable(
            default = supplyFuelAmount.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { supplyFuelAmount.value },
        ) {
            supplyFuelAmount.value = it
        }

        nestedTable.add(supplyFuelSliderButton)

        return nestedTable
    }

    private fun createBuildForeignFuelFactoryTable(): Table {
        val nestedTable = Table()

        val ownerId = createIntTextField(
            if (game.universeClient.isNewSelectedPlayerIdValid()) {
                game.universeClient.newSelectedPlayerId
            } else {
                game.universeClient.getCurrentPlayerData().playerId
            },
            gdxSettings.smallFontSize
        )

        val storedFuelRestMass = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val numBuilding = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build fuel factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildForeignFuelFactoryCommand = BuildForeignFuelFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                senderTopLeaderId = game.universeClient.getCurrentPlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = ownerId.value,
                fuelFactoryInternalData = game.universeClient.getCurrentPlayerData()
                    .playerInternalData.playerScienceData()
                    .playerScienceApplicationData.newFuelFactoryInternalData(),
                storedFuelRestMass = storedFuelRestMass.value,
                numBuilding = numBuilding.value
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
                "New factory stored fuel: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(storedFuelRestMass.textField)

        nestedTable.row().space(10f)

        val storedFuelRestMassDoubleSliderButton = createDoubleSliderButtonTable(
            default = storedFuelRestMass.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { storedFuelRestMass.value },
        ) {
            storedFuelRestMass.value = it
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
            default = numBuilding.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { numBuilding.value },
        ) {
            numBuilding.value = it
        }

        nestedTable.add(numBuildingDoubleSliderButton).colspan(2)

        return nestedTable
    }

    private fun createBuildForeignResourceFactoryTable(): Table {
        val nestedTable = Table()

        val ownerId = createIntTextField(
            if (game.universeClient.isNewSelectedPlayerIdValid()) {
                game.universeClient.newSelectedPlayerId
            } else {
                game.universeClient.getCurrentPlayerData().playerId
            },
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
            1.0,
            gdxSettings.smallFontSize
        )

        // entertainment resource is not produced by factory
        val outputResourceSelectBox = createSelectBox(
            ResourceType.factoryResourceList,
            ResourceType.values().first(),
            gdxSettings.smallFontSize
        )

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build resource factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildForeignResourceFactoryCommand = BuildForeignResourceFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                senderTopLeaderId = game.universeClient.getCurrentPlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = ownerId.value,
                resourceFactoryInternalData = game.universeClient.getCurrentPlayerData()
                    .playerInternalData.playerScienceData()
                    .playerScienceApplicationData.newResourceFactoryInternalData(
                        outputResourceSelectBox.selected,
                        qualityLevel.value,
                    ),
                qualityLevel = qualityLevel.value,
                storedFuelRestMass = storedFuelRestMass.value,
                numBuilding = numBuilding.value
            )

            game.universeClient.currentCommand = buildForeignResourceFactoryCommand
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
                "Output resource: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(outputResourceSelectBox)

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
            qualityLevel.value = Notation.roundDecimal(fl.toDouble(), 2)
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
            default = storedFuelRestMass.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { storedFuelRestMass.value },
        ) {
            storedFuelRestMass.value = it
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
            default = numBuilding.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { numBuilding.value },
        ) {
            numBuilding.value = it
        }

        nestedTable.add(numBuildingDoubleSliderButton).colspan(2)

        return nestedTable
    }


    private fun createBuildLocalFuelFactoryTable(): Table {
        val nestedTable = Table()

        val storedFuelRestMass = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val numBuilding = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build local fuel factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildLocalFuelFactoryCommand = BuildLocalFuelFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetCarrierId = carrierId,
                numBuilding = numBuilding.value
            )

            game.universeClient.currentCommand = buildLocalFuelFactoryCommand
        }
        nestedTable.add(buildForeignFuelFactoryTextButton).colspan(2)

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
            default = storedFuelRestMass.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { storedFuelRestMass.value },
        ) {
            storedFuelRestMass.value = it
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
            default = numBuilding.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { numBuilding.value },
        ) {
            numBuilding.value = it
        }

        nestedTable.add(numBuildingDoubleSliderButton).colspan(2)

        return nestedTable
    }

    private fun createBuildLocalResourceFactoryTable(): Table {
        val nestedTable = Table()

        val qualityLevel = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        val storedFuelRestMass = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val numBuilding = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        // Entertainment resource is not produced by factory
        val outputResourceSelectBox = createSelectBox(
            ResourceType.factoryResourceList,
            ResourceType.values().first(),
            gdxSettings.smallFontSize
        )

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build local resource factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildLocalResourceFactoryCommand = BuildLocalResourceFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                outputResourceType = outputResourceSelectBox.selected,
                targetCarrierId = carrierId,
                qualityLevel = qualityLevel.value,
                numBuilding = numBuilding.value
            )

            game.universeClient.currentCommand = buildLocalResourceFactoryCommand
        }
        nestedTable.add(buildForeignFuelFactoryTextButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Output resource: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(outputResourceSelectBox)

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
            qualityLevel.value = Notation.roundDecimal(fl.toDouble(), 2)
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
            default = storedFuelRestMass.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { storedFuelRestMass.value },
        ) {
            storedFuelRestMass.value = it
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
            default = numBuilding.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { numBuilding.value },
        ) {
            numBuilding.value = it
        }

        nestedTable.add(numBuildingDoubleSliderButton).colspan(2)

        return nestedTable
    }

    private fun createScholarTable(scholarPopData: ScholarPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Scholar data: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Institutes: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(createInstituteMapTable(scholarPopData))

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Build institute commands: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createBuildInstituteTable())

        return nestedTable
    }

    private fun createInstituteMapTable(scholarPopData: ScholarPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Institute id: ",
                gdxSettings.smallFontSize
            )
        )

        val instituteSelectBox = createSelectBox(
            scholarPopData.instituteMap.keys.toList(),
            instituteId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            instituteId = id
            updateCarrierTable()
        }
        nestedTable.add(instituteSelectBox)

        nestedTable.row().space(10f)

        if (scholarPopData.instituteMap.containsKey(instituteSelectBox.selected)) {
            val institute: InstituteData = scholarPopData.instituteMap.getValue(
                instituteSelectBox.selected
            )

            nestedTable.add(
                createInstituteTable(
                    instituteSelectBox.selected,
                    institute,
                )
            ).colspan(2)
        }


        return nestedTable
    }

    private fun createInstituteTable(
        instituteId: Int,
        instituteData: InstituteData,
    ): Table {
        val nestedTable = Table()

        val removeInstituteButton = createTextButton(
            "Remove institute",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val removeInstituteCommand = RemoveInstituteCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierId = carrierId,
                instituteId = instituteId
            )

            game.universeClient.currentCommand = removeInstituteCommand
        }
        nestedTable.add(removeInstituteButton)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge x: ${instituteData.instituteInternalData.xCor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge y: ${instituteData.instituteInternalData.yCor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge range: ${instituteData.instituteInternalData.range}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. equipment consumption: ${instituteData.instituteInternalData.researchEquipmentPerTime}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ${instituteData.instituteInternalData.maxNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last employee: ${instituteData.lastNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Strength: ${instituteData.strength}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createBuildInstituteTable(): Table {
        val nestedTable = Table()

        val xCor = createDoubleTextField(
            default = game.universeClient.selectedKnowledgeDouble2D.x,
            fontSize = gdxSettings.smallFontSize
        )

        val yCor = createDoubleTextField(
            default = game.universeClient.selectedKnowledgeDouble2D.y,
            fontSize = gdxSettings.smallFontSize
        )

        val range = createDoubleTextField(
            default = 0.0,
            fontSize = gdxSettings.smallFontSize
        )

        val researchEquipmentPerTime = createDoubleTextField(
            default = 0.25,
            fontSize = gdxSettings.smallFontSize
        )

        val maxNumEmployee = createDoubleTextField(
            default = 0.0,
            fontSize = gdxSettings.smallFontSize
        )

        val buildInstituteButton = createTextButton(
            "Build institute",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildInstituteCommand = BuildInstituteCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierId = carrierId,
                instituteInternalData = InstituteInternalData(
                    xCor = xCor.value,
                    yCor = yCor.value,
                    range = range.value,
                    researchEquipmentPerTime = researchEquipmentPerTime.value,
                    maxNumEmployee = maxNumEmployee.value,
                )
            )

            game.universeClient.currentCommand = buildInstituteCommand
        }
        nestedTable.add(buildInstituteButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New institute knowledge x: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(xCor.textField)

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "New institute knowledge y: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(yCor.textField)

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "New institute knowledge range: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(range.textField)

        nestedTable.row().space(10f)

        val rangeSliderButtonTable = createDoubleSliderButtonTable(
            default = range.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { range.value }
        ) {
            range.value = it
        }
        nestedTable.add(rangeSliderButtonTable).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. equipment consumption: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(researchEquipmentPerTime.textField)

        nestedTable.row().space(10f)

        val researchEquipmentPerTimeSliderButtonTable = createDoubleSliderButtonTable(
            default = researchEquipmentPerTime.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { researchEquipmentPerTime.value }
        ) {
            researchEquipmentPerTime.value = it
        }
        nestedTable.add(researchEquipmentPerTimeSliderButtonTable).colspan(2)


        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(maxNumEmployee.textField)

        nestedTable.row().space(10f)

        val maxNumEmployeeSliderButtonTable = createDoubleSliderButtonTable(
            default = maxNumEmployee.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { maxNumEmployee.value }
        ) {
            maxNumEmployee.value = it
        }
        nestedTable.add(maxNumEmployeeSliderButtonTable).colspan(2)

        return nestedTable
    }


    private fun createEngineerTable(engineerPopData: EngineerPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Engineer data: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Laboratories: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(createLaboratoryMapTable(engineerPopData))

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Build laboratory commands: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createBuildLaboratoryTable())

        return nestedTable
    }

    private fun createLaboratoryMapTable(engineerPopData: EngineerPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Laboratory id: ",
                gdxSettings.smallFontSize
            )
        )

        val laboratorySelectBox = createSelectBox(
            engineerPopData.laboratoryMap.keys.toList(),
            laboratoryId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            laboratoryId = id
            updateCarrierTable()
        }
        nestedTable.add(laboratorySelectBox)

        nestedTable.row().space(10f)

        if (engineerPopData.laboratoryMap.containsKey(laboratorySelectBox.selected)) {
            val laboratory: LaboratoryData = engineerPopData.laboratoryMap.getValue(
                laboratorySelectBox.selected
            )

            nestedTable.add(
                createLaboratoryTable(
                    laboratorySelectBox.selected,
                    laboratory
                )
            ).colspan(2)
        }


        return nestedTable
    }

    private fun createLaboratoryTable(
        laboratoryId: Int,
        laboratoryData: LaboratoryData,
    ): Table {
        val nestedTable = Table()

        val removeLaboratoryButton = createTextButton(
            "Remove laboratory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val removeLaboratoryCommand = RemoveLaboratoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierId = carrierId,
                laboratoryId = laboratoryId
            )

            game.universeClient.currentCommand = removeLaboratoryCommand
        }
        nestedTable.add(removeLaboratoryButton)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge x: ${laboratoryData.laboratoryInternalData.xCor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge y: ${laboratoryData.laboratoryInternalData.yCor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Knowledge range: ${laboratoryData.laboratoryInternalData.range}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. equipment consumption: ${laboratoryData.laboratoryInternalData.researchEquipmentPerTime}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ${laboratoryData.laboratoryInternalData.maxNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Last employee: ${laboratoryData.lastNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Strength: ${laboratoryData.strength}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createBuildLaboratoryTable(): Table {
        val nestedTable = Table()

        val xCor = createDoubleTextField(
            default = game.universeClient.selectedKnowledgeDouble2D.x,
            fontSize = gdxSettings.smallFontSize
        )

        val yCor = createDoubleTextField(
            default = game.universeClient.selectedKnowledgeDouble2D.y,
            fontSize = gdxSettings.smallFontSize
        )

        val range = createDoubleTextField(
            default = 0.0,
            fontSize = gdxSettings.smallFontSize
        )

        val researchEquipmentPerTime = createDoubleTextField(
            default = 0.25,
            fontSize = gdxSettings.smallFontSize
        )

        val maxNumEmployee = createDoubleTextField(
            default = 0.0,
            fontSize = gdxSettings.smallFontSize
        )

        val buildLaboratoryButton = createTextButton(
            "Build laboratory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildLaboratoryCommand = BuildLaboratoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierId = carrierId,
                laboratoryInternalData = LaboratoryInternalData(
                    xCor = xCor.value,
                    yCor = yCor.value,
                    range = range.value,
                    researchEquipmentPerTime = researchEquipmentPerTime.value,
                    maxNumEmployee = maxNumEmployee.value,
                )
            )

            game.universeClient.currentCommand = buildLaboratoryCommand
        }
        nestedTable.add(buildLaboratoryButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New laboratory knowledge x: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(xCor.textField)

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "New laboratory knowledge y: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(yCor.textField)

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "New laboratory knowledge range: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(range.textField)

        nestedTable.row().space(10f)

        val rangeSliderButtonTable = createDoubleSliderButtonTable(
            default = range.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { range.value }
        ) {
            range.value = it
        }
        nestedTable.add(rangeSliderButtonTable).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. equipment consumption: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(researchEquipmentPerTime.textField)

        nestedTable.row().space(10f)

        val researchEquipmentPerTimeSliderButtonTable = createDoubleSliderButtonTable(
            default = researchEquipmentPerTime.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { researchEquipmentPerTime.value }
        ) {
            researchEquipmentPerTime.value = it
        }
        nestedTable.add(researchEquipmentPerTimeSliderButtonTable).colspan(2)


        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Max. employee: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(maxNumEmployee.textField)

        nestedTable.row().space(10f)

        val maxNumEmployeeSliderButtonTable = createDoubleSliderButtonTable(
            default = maxNumEmployee.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { maxNumEmployee.value }
        ) {
            maxNumEmployee.value = it
        }
        nestedTable.add(maxNumEmployeeSliderButtonTable).colspan(2)

        return nestedTable
    }

    private fun createServiceTable(servicePopData: ServicePopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Service worker data: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Export centers: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createExportCenterMapTable(servicePopData))

        nestedTable.row().space(30f)

        nestedTable.add(
            createLabel(
                "Buy resource commands: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(createPlayerBuyResourceTable())

        return nestedTable
    }

    private fun createExportCenterMapTable(servicePopData: ServicePopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Export center owner id: ",
                gdxSettings.smallFontSize
            )
        )

        val exportCenterOwnerIdSelectBox = createSelectBox(
            servicePopData.exportData.playerExportCenterMap.keys.toList(),
            exportCenterOwnerId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            exportCenterOwnerId = id
            updateCarrierTable()
        }
        nestedTable.add(exportCenterOwnerIdSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Export center target id: ",
                gdxSettings.smallFontSize
            )
        )

        val exportCenterTargetIdSelectBox = createSelectBox(
            servicePopData.exportData.playerExportCenterMap.getOrDefault(
                exportCenterOwnerId,
                PlayerExportCenterData()
            ).exportDataList.map { it.targetPlayerId },
            exportCenterTargetId,
            gdxSettings.smallFontSize
        ) { id, _ ->
            exportCenterTargetId = id
            updateCarrierTable()
        }
        nestedTable.add(exportCenterTargetIdSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Export center resource: ",
                gdxSettings.smallFontSize
            )
        )

        val exportCenterResourceTypeSelectBox = createSelectBox(
            servicePopData.exportData.playerExportCenterMap.getOrDefault(
                exportCenterOwnerId,
                PlayerExportCenterData()
            ).getResourceTypeList(exportCenterTargetId),
            exportCenterResourceType,
            gdxSettings.smallFontSize
        ) { type, _ ->
            exportCenterResourceType = type
            updateCarrierTable()
        }
        nestedTable.add(exportCenterResourceTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Export center resource quality class: ",
                gdxSettings.smallFontSize
            )
        )

        val exportCenterResourceQualityClassSelectBox = createSelectBox(
            servicePopData.exportData.playerExportCenterMap.getOrDefault(
                exportCenterOwnerId,
                PlayerExportCenterData()
            ).getResourceQualityClassList(exportCenterTargetId, exportCenterResourceType),
            exportCenterResourceQualityClass,
            gdxSettings.smallFontSize
        ) { qualityClass, _ ->
            exportCenterResourceQualityClass = qualityClass
            updateCarrierTable()
        }
        nestedTable.add(exportCenterResourceQualityClassSelectBox)

        if (servicePopData.exportData.playerExportCenterMap.containsKey(exportCenterOwnerId)) {
            val exportCenterData: PlayerExportCenterData = servicePopData
                .exportData.playerExportCenterMap.getValue(
                    exportCenterOwnerId
                )

            val singleExportList: List<PlayerSingleExportData> = exportCenterData.getExportDataList(
                targetPlayerId = exportCenterTargetId,
                resourceType = exportCenterResourceType,
                resourceQualityClass = exportCenterResourceQualityClass,
            )

            singleExportList.forEach {
                nestedTable.row().space(20f)

                nestedTable.add(
                    createSingleExportDataTable(it)
                ).colspan(2)
            }
        }

        return nestedTable
    }

    private fun createSingleExportDataTable(playerSingleExportData: PlayerSingleExportData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Amount per time: ${playerSingleExportData.amountPerTime}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Stored fuel: ${playerSingleExportData.storedFuelRestMass}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createPlayerBuyResourceTable(): Table {
        val nestedTable = Table()

        val targetPlayerId = createIntTextField(
            game.universeClient.getCurrentPlayerData().playerId,
            gdxSettings.smallFontSize
        )

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            ResourceType.values().first(),
            gdxSettings.smallFontSize
        )

        val resourceQualityClassSelectBox = createSelectBox(
            ResourceQualityClass.values().toList(),
            ResourceQualityClass.values().first(),
            gdxSettings.smallFontSize
        )

        val fuelRestMass = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val amountPerTime = createDoubleTextField(
            0.0,
            gdxSettings.smallFontSize
        )

        val buyButton = createTextButton(
            "Buy",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val playerBuyResourceCommand = PlayerBuyResourceCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                targetTopLeaderId = playerData.topLeaderId(),
                targetCarrierId = carrierId,
                targetPlayerIdOfExportCenter = targetPlayerId.value,
                resourceType = resourceTypeSelectBox.selected,
                resourceQualityClass = resourceQualityClassSelectBox.selected,
                fuelRestMassAmount = fuelRestMass.value,
                amountPerTime = amountPerTime.value,
                senderFuelLossFractionPerDistance = playerData.playerInternalData.playerScienceData()
                    .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
            )

            game.universeClient.currentCommand = playerBuyResourceCommand
        }
        nestedTable.add(buyButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Export to: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(targetPlayerId.textField)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Resource: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(resourceTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Quality class: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(resourceQualityClassSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Send fuel amount: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(fuelRestMass.textField)

        nestedTable.row().space(10f)

        val fuelRestMassSliderButtonTable = createDoubleSliderButtonTable(
            default = fuelRestMass.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { fuelRestMass.value }
        ) {
            fuelRestMass.value = it
        }
        nestedTable.add(fuelRestMassSliderButtonTable).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Amount per time: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(amountPerTime.textField)

        nestedTable.row().space(10f)

        val amountPerTimeSliderButtonTable = createDoubleSliderButtonTable(
            default = amountPerTime.value,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { amountPerTime.value }
        ) {
            amountPerTime.value = it
        }
        nestedTable.add(amountPerTimeSliderButtonTable).colspan(2)

        return nestedTable
    }

    private fun createSoldierTable(soldierPopData: SoldierPopData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Soldier data: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Attack: ${soldierPopData.militaryBaseData.attack}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "Shield: ${soldierPopData.militaryBaseData.shield}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)


        nestedTable.add(
            createLabel(
                "Last employee: ${soldierPopData.militaryBaseData.lastNumEmployee}",
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createNewCarrierTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "New carrier commands: ",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val newCarrierQuality = createDoubleTextField(1.0, gdxSettings.smallFontSize)
        val buildCarrierButton = createTextButton(
            "Build carrier",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val buildLocalCarrierCommand = BuildLocalCarrierCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                qualityLevel = newCarrierQuality.value,
            )

            game.universeClient.currentCommand = buildLocalCarrierCommand
        }

        nestedTable.add(buildCarrierButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Quality: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(newCarrierQuality.textField)

        nestedTable.row().space(10f)

        val newCarrierQualitySlider = createSlider(
            0f,
            1f,
            0.01f,
            1f
        ) { fl, _ ->
            newCarrierQuality.value = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(newCarrierQualitySlider).colspan(2)

        return nestedTable
    }

    private fun createNewPlayerTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "New player commands: ",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(10f)

        val newPlayerCarrierIdSet: MutableSet<Int> = mutableSetOf()

        val newPlayerCarrierIdSelectBoxContainer = Container(
            createSelectBox(
                newPlayerCarrierIdSet.toList(),
                newPlayerCarrierIdSet.firstOrNull() ?: -1,
                gdxSettings.smallFontSize
            )
        )

        val resourceFraction = createDoubleTextField(
            1.0,
            gdxSettings.smallFontSize
        )

        val splitCarrierButton = createTextButton(
            "Split carrier",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            val splitCarrierCommand = SplitCarrierCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                carrierIdList = newPlayerCarrierIdSet.toList(),
                resourceFraction = resourceFraction.value,
            )

            game.universeClient.currentCommand = splitCarrierCommand
        }

        nestedTable.add(splitCarrierButton).colspan(2)

        nestedTable.row().space(10f)

        val newPlayerCarrierIdLabel = createLabel(
            "Carriers in new player: $newPlayerCarrierIdSet",
            gdxSettings.smallFontSize
        )
        newPlayerCarrierIdLabel.wrap = true
        nestedTable.add(newPlayerCarrierIdLabel).colspan(2).left()

        nestedTable.row().space(10f)

        val addButton = createTextButton(
            "Add",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            newPlayerCarrierIdSet.add(carrierId)
            newPlayerCarrierIdLabel.setText("Carriers in new player: $newPlayerCarrierIdSet")
            newPlayerCarrierIdSelectBoxContainer.actor = createSelectBox(
                newPlayerCarrierIdSet.toList(),
                newPlayerCarrierIdSet.firstOrNull() ?: -1,
                gdxSettings.smallFontSize
            )
        }
        nestedTable.add(addButton).colspan(2)

        nestedTable.row().space(10f)

        val removeButton = createTextButton(
            "Remove",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            newPlayerCarrierIdSet.remove(newPlayerCarrierIdSelectBoxContainer.actor.selected)
            newPlayerCarrierIdLabel.setText("Carriers in new player: $newPlayerCarrierIdSet")
            newPlayerCarrierIdSelectBoxContainer.actor = createSelectBox(
                newPlayerCarrierIdSet.toList(),
                newPlayerCarrierIdSet.firstOrNull() ?: -1,
                gdxSettings.smallFontSize
            )
        }
        nestedTable.add(removeButton)

        nestedTable.add(newPlayerCarrierIdSelectBoxContainer)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Resource fraction: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(resourceFraction.textField)

        nestedTable.row().space(10f)

        val resourceFractionSlider = createSlider(
            0f,
            1f,
            0.01f,
            0.2f,
        ) { fl, _ ->
            resourceFraction.value = Notation.roundDecimal(fl.toDouble(), 2)
        }
        nestedTable.add(resourceFractionSlider).colspan(2)

        return nestedTable
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}