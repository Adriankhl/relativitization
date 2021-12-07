package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

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

        val headerLabel = createLabel(
            "Pop system: player ${playerData.playerId}",
            gdxSettings.bigFontSize
        )
        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        table.add(createLabel("Carrier:", gdxSettings.normalFontSize))

        table.row()

        val carrierSelectBox = createSelectBox(
            playerData.playerInternalData.popSystemData().carrierDataMap.keys.toList(),
            carrierId,
            gdxSettings.smallFontSize,
        ) { id, _ ->
            carrierId = id
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

        val carrierTypeLabel =
            createLabel("Carrier type: ${carrier.carrierType}", gdxSettings.smallFontSize)
        carrierTable.add(carrierTypeLabel)

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
        }
        carrierTable.add(popTypeSelectBox)

        carrierTable.row().space(30f)

        carrierTable.add(createPopTable(carrier.allPopData))
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

        return nestedTable
    }

    private fun createCommonPopTable(commonPopData: CommonPopData): Table {
        val nestedTable = Table()

        nestedTable.add(createLabel("Common pop data: ", gdxSettings.normalFontSize))

        nestedTable.row().space(10f)

        nestedTable.add(createLabel(
            "Adult population: ${commonPopData.adultPopulation}",
            gdxSettings.smallFontSize
        ))

        nestedTable.row().space(10f)

        nestedTable.add(createLabel(
            "Unemployment rate: ${commonPopData.unemploymentRate}",
            gdxSettings.smallFontSize
        ))

        nestedTable.row().space(10f)

        nestedTable.add(createLabel(
            "Satisfaction: ${commonPopData.satisfaction}",
            gdxSettings.smallFontSize
        ))

        nestedTable.row().space(10f)

        nestedTable.add(createLabel(
            "Salary: ${commonPopData.salary}",
            gdxSettings.smallFontSize
        ))

        nestedTable.row().space(10f)

        nestedTable.add(createTargetSalaryTable(commonPopData.salary))

        nestedTable.row().space(10f)

        return nestedTable
    }

    private fun createTargetSalaryTable(default: Double): Table {
        val nestedTable = Table()

        return nestedTable
    }
}