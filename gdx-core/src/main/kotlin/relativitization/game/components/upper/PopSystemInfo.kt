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
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.properties.Delegates

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


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
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

        val onTargetSalaryChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
        var targetSalary: Double by Delegates.observable(defaultSalary) { _, _, _ ->
            onTargetSalaryChangeFunctionList.forEach { it() }
        }
        val targetSalaryTextField = createTextField(
            targetSalary.toString(),
            gdxSettings.smallFontSize
        ) { s, _ ->
            val newTargetSalary: Double = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid target salary")
                targetSalary
            }

            if (newTargetSalary != targetSalary) {
                logger.debug("New target salary: $newTargetSalary")
                targetSalary = newTargetSalary
            }
        }
        onTargetSalaryChangeFunctionList.add {
            targetSalaryTextField.text = targetSalary.toString()
        }

        nestedTable.add(
            createLabel(
                "Target salary: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(targetSalaryTextField)

        nestedTable.row().space(10f)

        val targetSalarySliderButtonTable = createDoubleSliderButtonTable(
            default = targetSalary,
            sliderStepSize = 0.01f,
            sliderDecimalPlace = 2,
            buttonSize = 40f * gdxSettings.imageScale,
            buttonSoundVolume = gdxSettings.soundEffectsVolume,
            currentValue = { targetSalary }
        ) {
            targetSalary = it
        }
        nestedTable.add(targetSalarySliderButtonTable).colspan(2)

        nestedTable.row().space(10f)

        val changeSalaryTextButton = createTextButton(
            text = "Change salary",
            fontSize = gdxSettings.smallFontSize,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            val changeSalaryCommand = ChangeSalaryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                carrierId = carrierId,
                popType = popType,
                salary = targetSalary,
            )

            game.universeClient.currentCommand = changeSalaryCommand
        }
        nestedTable.add(changeSalaryTextButton).colspan(2)

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

        val onQualityLevelChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
        var qualityLevel: Double by Delegates.observable(1.0) { _, _, _ ->
            onQualityLevelChangeFunctionList.forEach { it() }
        }
        val qualityLevelTextField = createTextField(
            default = qualityLevel.toString(),
            fontSize = gdxSettings.smallFontSize,
        ) { s, _ ->
            val newQualityLevel: Double = try {
                s.toDouble()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid quality level")
                qualityLevel
            }

            if (newQualityLevel != qualityLevel) {
                logger.debug("New quality level: $newQualityLevel")
                qualityLevel = newQualityLevel
            }
        }
        onQualityLevelChangeFunctionList.add {
            qualityLevelTextField.text = qualityLevel.toString()
        }

        val onOwnerIdChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
        var ownerId: Int by Delegates.observable(1) { _, _, _ ->
            onOwnerIdChangeFunctionList.forEach { it() }
        }
        val ownerIdTextField = createTextField(
            default = playerData.playerId.toString(),
            fontSize = gdxSettings.smallFontSize,
        ) { s, _ ->
            val newOwnerId: Int = try {
                s.toInt()
            } catch (e: NumberFormatException) {
                logger.debug("Invalid ownerId")
                ownerId
            }

            if (newOwnerId != ownerId) {
                logger.debug("New ownerId: $newOwnerId")
                ownerId = newOwnerId
            }
        }
        onOwnerIdChangeFunctionList.add {
            ownerIdTextField.text = ownerId.toString()
        }


        nestedTable.add(
            createLabel(
                "Labourer:",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "New factory owner: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(ownerIdTextField)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "New factory quality level: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(qualityLevelTextField)

        nestedTable.row().space(10f)

        val buildForeignFuelFactoryTextButton = createTextButton(
            "Build factory",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val buildForeignFuelFactoryCommand = BuildForeignFuelFactoryCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getUniverseData3D().getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getUniverseData3D().getCurrentPlayerData().int4D,
                senderTopLeaderId = game.universeClient.getUniverseData3D()
                    .getCurrentPlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = ownerId,
                fuelFactoryInternalData = playerData.playerInternalData.playerScienceData()
                    .playerScienceApplicationData.newFuelFactoryInternalData(qualityLevel),
                qualityLevel = qualityLevel,
                storedFuelRestMass = 0.0,
                numBuilding = 0.0
            )

            game.universeClient.currentCommand = buildForeignFuelFactoryCommand
        }
        nestedTable.add(buildForeignFuelFactoryTextButton).colspan(2)


        return nestedTable
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}