package relativitization.game.components.info

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ActorFunction
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.science.knowledge.AppliedResearchField
import relativitization.universe.data.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.science.knowledge.BasicResearchField
import relativitization.universe.data.science.knowledge.BasicResearchProjectData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.max
import kotlin.math.min

class KnowledgeMapInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val knowledgeBar: Table = Table()

    private val knowledgeGroup: Group = Group()

    private val knowledgeGroupScrollPane: ScrollPane = createScrollPane(knowledgeGroup)

    private val table: Table = Table()

    // the currently viewing player data
    private var playerData: PlayerData = PlayerData(-1)

    // zoom in knowledge map, fix icon size
    private val zoomInButton: ImageButton = createImageButton(
        name = "basic/white-zoom-in",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.knowledgeMapZoomRelativeToFullMap *= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    // zoom out knowledge map, fix icon size
    private val zoomOutButton: ImageButton = createImageButton(
        name = "basic/white-zoom-out",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.knowledgeMapZoomRelativeToFullMap /= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    // increase knowledge project icon size
    private val plusButton: ImageButton = createImageButton(
        name = "basic/white-plus",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.knowledgeMapProjectIconZoom *= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }

    // decrease knowledge project icon size
    private val minusButton: ImageButton = createImageButton(
        name = "basic/white-minus",
        rUp = 1.0f,
        gUp = 1.0f,
        bUp = 1.0f,
        aUp = 1.0f,
        rDown = 1.0f,
        gDown = 1.0f,
        bDown = 1.0f,
        aDown = 0.7f,
        rChecked = 1.0f,
        gChecked = 1.0f,
        bChecked = 1.0f,
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        gdxSettings.knowledgeMapProjectIconZoom /= gdxSettings.mapZoomFactor
        game.changeGdxSettings()
    }


    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        knowledgeGroupScrollPane.fadeScrollBars = false
        knowledgeGroupScrollPane.setClamp(true)
        knowledgeGroupScrollPane.setOverscroll(false, false)

        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        // No need to update full table, which includes the knowledge bar
        updateKnowledgeGroup()
    }

    override fun onGdxSettingsChange() {
        updateKnowledgeGroup()
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

        updateKnowledgeBar()

        updateKnowledgeGroup()

        table.add(knowledgeBar)

        table.row().space(20f)

        table.add(knowledgeGroupScrollPane).grow()

        table.row().space(20f)
    }

    private fun updateKnowledgeBar() {
        knowledgeBar.clear()

        val headerLabel = createLabel("Science: player ${playerData.id}", gdxSettings.bigFontSize)

        val controlTable: Table = Table()

        val controlScrollPane: ScrollPane = createScrollPane(controlTable)

        controlTable.add(zoomInButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        controlTable.add(zoomOutButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        controlTable.add(plusButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        controlTable.add(minusButton).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        knowledgeBar.add(headerLabel)

        knowledgeBar.row().space(20f)

        knowledgeBar.add(controlScrollPane).minHeight(controlTable.prefHeight)
    }

    private fun updateKnowledgeGroup() {
        knowledgeGroup.clear()

        knowledgeGroup.setSize(
            knowledgeMapWidthWithMargin().toFloat() * actualZoom(),
            knowledgeMapHeightWithMargin().toFloat() * actualZoom()
        )

        playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.forEach {
            val image: Image = createBasicProjectImage(it)

            logger.debug("Add basic research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        playerData.playerInternalData.playerScienceData.doneAppliedResearchProjectList.forEach {
            val image: Image = createAppliedProjectImage(it)

            logger.debug("Add applied research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        knowledgeGroupScrollPane.actor = knowledgeGroup
    }

    /**
     * Create image of a basic research project
     */
    private fun createBasicProjectImage(project: BasicResearchProjectData): Image {

        val rgb: Color = when (project.basicResearchField) {
            BasicResearchField.MATHEMATICS -> Color.GREEN
            BasicResearchField.PHYSICS -> Color.BLUE
            BasicResearchField.COMPUTER_SCIENCE -> Color.CYAN
            BasicResearchField.LIFE_SCIENCE -> Color.YELLOW
            BasicResearchField.SOCIAL_SCIENCE -> Color.BROWN
            BasicResearchField.HUMANITY -> Color.RED
        }

        return createImage(
            name = "science/book1",
            xPos = ((project.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom() -
                    projectImageDimension() * 0.5
                    ).toFloat(),
            yPos = ((project.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom() -
                    projectImageDimension() * 0.5
                    ).toFloat(),
            width = projectImageDimension().toFloat(),
            height = projectImageDimension().toFloat(),
            r = rgb.r,
            g = rgb.g,
            b = rgb.b,
            a = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {}
    }


    /**
     * Create image of a basic research project
     */
    private fun createAppliedProjectImage(project: AppliedResearchProjectData): Image {

        val rgb: Color = when (project.appliedResearchField) {
            AppliedResearchField.ENERGY_TECHNOLOGY -> Color.BLUE
            AppliedResearchField.FOOD_TECHNOLOGY -> Color.PINK
            AppliedResearchField.BIOMEDICAL_TECHNOLOGY -> Color.YELLOW
            AppliedResearchField.CHEMICAL_TECHNOLOGY -> Color.ORANGE
            AppliedResearchField.ENVIRONMENTAL_TECHNOLOGY -> Color.GREEN
            AppliedResearchField.ARCHITECTURE_TECHNOLOGY -> Color.GRAY
            AppliedResearchField.MACHINERY_TECHNOLOGY -> Color.BROWN
            AppliedResearchField.MATERIAL_TECHNOLOGY -> Color.GOLD
            AppliedResearchField.INFORMATION_TECHNOLOGY -> Color.FOREST
            AppliedResearchField.ART_TECHNOLOGY -> Color.RED
            AppliedResearchField.MILITARY_TECHNOLOGY -> Color.NAVY
        }

        return createImage(
            name = "science/wrench1",
            xPos = ((project.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom() -
                    projectImageDimension() * 0.5
                    ).toFloat(),
            yPos = ((project.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom() -
                    projectImageDimension() * 0.5
                    ).toFloat(),
            width = projectImageDimension().toFloat(),
            height = projectImageDimension().toFloat(),
            r = rgb.r,
            g = rgb.g,
            b = rgb.b,
            a = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {}
    }


    /**
     * The dimension of the icon of a knowledge project
     */
    private fun projectImageDimension(): Double {
        val image = ActorFunction.createImage(assets, "science/book1", 0.0f)
        val dim: Double = max(image.width, image.height).toDouble()
        return dim * gdxSettings.knowledgeMapProjectIconZoom
    }

   /**
     * Min x coordinate of the knowledge map
     */
    private fun knowledgeMapMinX(): Double {
        // Compute the dimension of knowledge map
        val minBasicX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.xCor
        } ?: -1.0
        val minAppliedX: Double = playerData.playerInternalData.playerScienceData.doneAppliedResearchProjectList.minOfOrNull {
            it.xCor
        } ?: -1.0

        return min(minBasicX, minAppliedX)
    }

    /**
     * Max x coordinate of the knowledge map
     */
    private fun knowledgeMapMaxX(): Double {
        // Compute the dimension of knowledge map
        val maxBasicX: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.xCor
        } ?: 1.0
        val maxAppliedX: Double = playerData.playerInternalData.playerScienceData.doneAppliedResearchProjectList.maxOfOrNull {
            it.xCor
        } ?: 1.0

        return max(maxBasicX, maxAppliedX)
    }

    /**
     * Min y coordinate of the knowledge map
     */
    private fun knowledgeMapMinY(): Double {
        // Compute the dimension of knowledge map
        val minBasicY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.minOfOrNull {
            it.yCor
        } ?: -1.0
        val minAppliedY: Double = playerData.playerInternalData.playerScienceData.doneAppliedResearchProjectList.minOfOrNull {
            it.yCor
        } ?: -1.0

        return min(minBasicY, minAppliedY)
    }


    /**
     * Max y coordinate of the knowledge map
     */
    private fun knowledgeMapMaxY(): Double {
        // Compute the dimension of knowledge map
        val maxBasicY: Double = playerData.playerInternalData.playerScienceData.doneBasicResearchProjectList.maxOfOrNull {
            it.yCor
        } ?: 1.0
        val maxAppliedY: Double = playerData.playerInternalData.playerScienceData.doneAppliedResearchProjectList.maxOfOrNull {
            it.yCor
        } ?: 1.0

        return max(maxBasicY, maxAppliedY)
    }

    /**
     * Compute the width of the knowledge map
     */
    private fun knowledgeMapWidth(): Double {
        val width = knowledgeMapMaxX() - knowledgeMapMinX()

        return if (width > 0.0) {
            width
        } else {
            logger.debug("width: $width, default to 1.0")
            1.0
        }
    }


    /**
     * Compute the height of the knowledge map
     */
    private fun knowledgeMapHeight(): Double {
        val height = knowledgeMapMaxY() - knowledgeMapMinY()

        return if (height > 0.0) {
            height
        } else {
            logger.debug("height: $height, default to 1.0")
            1.0
        }
    }


    /**
     * The margin around the knowledge map
     */
    private fun knowledgeMapMargin(): Double = max(knowledgeMapWidth(), knowledgeMapHeight()) * 0.5


    /**
     * The knowledge map width with margin
     */
    private fun knowledgeMapWidthWithMargin(): Double = knowledgeMapWidth() + 2 * knowledgeMapMargin()

    /**
     * The knowledge map height with margin
     */
    private fun knowledgeMapHeightWithMargin(): Double = knowledgeMapHeight() + 2 * knowledgeMapMargin()


    private fun actualZoom(): Float {
        // Actual zoom when mapZoomRelativeToFullMap equals 1.0
        val zoomOne = min(
            knowledgeGroupScrollPane.width / knowledgeMapWidthWithMargin().toFloat(),
            knowledgeGroupScrollPane.height / knowledgeMapHeightWithMargin().toFloat()
        )
        return zoomOne * gdxSettings.knowledgeMapZoomRelativeToFullMap
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}