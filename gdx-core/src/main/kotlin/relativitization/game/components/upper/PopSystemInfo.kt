package relativitization.game.components.upper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.ResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.FuelFactoryData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryData
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
                "Fuel factory: ",
                gdxSettings.smallFontSize
            )
        )

        val fuelFactorySelectBox = createSelectBox(
            labourerPopData.fuelFactoryMap.keys.toList(),
            labourerPopData.fuelFactoryMap.keys.firstOrNull() ?: -1,
            gdxSettings.smallFontSize
        ) { _, _ ->
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

        // Add supply fuel button if this factory is not self factory
        if (playerData.playerId != game.universeClient.getCurrentPlayerData().playerId) {

        }

        return nestedTable
    }

    private fun createResourceFactoryMapTable(labourerPopData: LabourerPopData): Table {
        val nestedTable = Table()

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            ResourceType.values().first(),
            gdxSettings.smallFontSize,
        ) { _, _ ->
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
            labourerPopData.resourceFactoryMap.filter {
                it.value.resourceFactoryInternalData.outputResource == resourceTypeSelectBox.selected
            }.keys.first(),
            gdxSettings.smallFontSize
        ) { _, _ ->
            updateCarrierTable()
        }

        nestedTable.add(
            createLabel(
                "Resource factory: ",
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

        resourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (resourceType, inputResource) ->
            val lastInputResource: InputResourceData = resourceFactoryData.lastInputResourceMap
                .getOrDefault(resourceType, InputResourceData())

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


        return nestedTable
    }

    private fun createBuildForeignFuelFactoryTable(): Table {
        val nestedTable = Table()

        val ownerId = createIntTextField(
            playerData.playerId,
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
                ownerId = ownerId.num,
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
            1.0,
            gdxSettings.smallFontSize
        )

        val outputResourceSelectBox = createSelectBox(
            ResourceType.values().toList(),
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
                ownerId = ownerId.num,
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

        val outputResourceSelectBox = createSelectBox(
            ResourceType.values().toList(),
            ResourceType.values().first(),
            gdxSettings.smallFontSize
        )

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build resource factory",
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

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}