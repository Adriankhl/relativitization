package relativitization.game.components.info

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ActorFunction
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Int3D
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
        updateTable()
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

        table.add(knowledgeGroupScrollPane).fill()

        table.row().space(20f)
    }

    private fun updateKnowledgeBar() {
        knowledgeBar.clear()

        val headerLabel = createLabel("Science: player ${playerData.id}", gdxSettings.bigFontSize)

        knowledgeBar.add(headerLabel)
    }

    private fun updateKnowledgeGroup() {
        knowledgeGroup.clear()

        knowledgeGroup.setSize(
            knowledgeMapWidth().toFloat() * actualZoom() + 2 * displayMargin(),
            knowledgeMapHeight().toFloat() * actualZoom() + 2 * displayMargin()
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
            xPos = ((project.xCor - knowledgeMapMinX()) * actualZoom() -
                    projectImageDimension() * 0.5 + displayMargin()
                    ).toFloat(),
            yPos = ((project.yCor - knowledgeMapMinY()) * actualZoom() -
                    projectImageDimension() * 0.5 + displayMargin()
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
            xPos = ((project.xCor - knowledgeMapMinX()) * actualZoom() -
                    projectImageDimension() * 0.5 + displayMargin()
                    ).toFloat(),
            yPos = ((project.yCor - knowledgeMapMinY()) * actualZoom() -
                    projectImageDimension() * 0.5 + displayMargin()
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
     * The margin around the knowledge map
     */
    private fun displayMargin(): Float = (projectImageDimension()).toFloat()

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

    private fun actualZoom(): Float {
        // Actual zoom when mapZoomRelativeToFullMap equals 1.0
        val zoomOne = min(
            knowledgeGroupScrollPane.width / knowledgeMapWidth().toFloat(),
            knowledgeGroupScrollPane.height / knowledgeMapHeight().toFloat()
        )
        return zoomOne * gdxSettings.knowledgeMapZoomRelativeToFullMap
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}