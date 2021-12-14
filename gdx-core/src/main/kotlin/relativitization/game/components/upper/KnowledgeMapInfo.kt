package relativitization.game.components.upper

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ActorFunction
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.physics.Double2D
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.InstituteData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
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

    // If true, the latest selected project is basic project
    private var isBasicProjectSelected: Boolean = true

    // If true, the latest selected project is applied project
    private var isAppliedProjectSelected: Boolean = false

    private var selectedBasicResearchProjectData: BasicResearchProjectData =
        BasicResearchProjectData(
            basicResearchId = 0,
            basicResearchField = BasicResearchField.MATHEMATICS,
            xCor = 0.0,
            yCor = 0.0,
            difficulty = 0.0,
            significance = 0.0,
            referenceBasicResearchIdList = listOf(),
            referenceAppliedResearchIdList = listOf()
        )

    private var selectedAppliedResearchProjectData: AppliedResearchProjectData =
        AppliedResearchProjectData(
            appliedResearchId = 0,
            appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
            xCor = 0.0,
            yCor = 0.0,
            difficulty = 0.0,
            significance = 0.0,
            referenceBasicResearchIdList = listOf(),
            referenceAppliedResearchIdList = listOf()
        )

    // If true, show institutes and laboratory on the knowledge map
    private var showInstituteAndLaboratory: Boolean = false

    // If true, a institute is selected by mouse
    private var isInstituteSelected: Boolean = true

    // If true, a laboratory is selected by mouse
    private var isLaboratorySelected: Boolean = true

    private var selectedInstituteData: InstituteData = InstituteData()

    private var selectedLaboratoryData: LaboratoryData = LaboratoryData()

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

    private val knowledgeProjectTable: Table = Table()

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

    override fun onUniverseData3DChange() {
        updatePlayerData()
        updateKnowledgeGroup()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        // No need to update full table, which includes the knowledge bar
        updateKnowledgeGroup()
    }

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onGdxSettingsChange() {
        updateKnowledgeGroup()
    }

    override fun onSelectedKnowledgeDouble2DChange() {
        updateKnowledgeProjectTable()
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

        updateKnowledgeProjectTable()

        updateKnowledgeBar()

        updateKnowledgeGroup()

        table.add(knowledgeBar)

        table.row().space(20f)

        table.add(knowledgeGroupScrollPane).grow()

        table.row().space(20f)
    }

    private fun updateKnowledgeBar() {
        knowledgeBar.clear()

        val headerLabel =
            createLabel("Science: player ${playerData.playerId}", gdxSettings.bigFontSize)

        val controlTable: Table = Table()

        controlTable.add(zoomInButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        controlTable.add(zoomOutButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        controlTable.add(plusButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        controlTable.add(minusButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)
        controlTable.add(knowledgeProjectTable).pad(20f)

        val controlScrollPane: ScrollPane = createScrollPane(controlTable)

        // Configure scroll pane
        controlScrollPane.fadeScrollBars = false
        controlScrollPane.setClamp(true)
        controlScrollPane.setOverscroll(false, false)

        knowledgeBar.add(headerLabel)

        knowledgeBar.row().space(20f)

        knowledgeBar.add(controlScrollPane).minHeight(controlTable.prefHeight)
    }

    private fun updateKnowledgeProjectTable() {
        knowledgeProjectTable.clear()

        val selectedKnowledgeMapDouble2D: Label = createLabel(
            "Position: (%.2f, %.2f)".format(
                game.universeClient.selectedKnowledgeDouble2D.x,
                game.universeClient.selectedKnowledgeDouble2D.y,
            ),
            gdxSettings.normalFontSize
        )

        val selectedProjectIdLabel: Label = when {
            isBasicProjectSelected && !isAppliedProjectSelected -> {
                createLabel(
                    "Basic Project Id: ${selectedBasicResearchProjectData.basicResearchId}",
                    gdxSettings.normalFontSize
                )
            }
            isAppliedProjectSelected && !isBasicProjectSelected -> {
                createLabel(
                    "Applied Project Id: ${selectedAppliedResearchProjectData.appliedResearchId}",
                    gdxSettings.normalFontSize
                )
            }
            isBasicProjectSelected && isAppliedProjectSelected -> {
                createLabel(
                    "Error: Both basic project and applied project are selected. ",
                    gdxSettings.normalFontSize
                )
            }
            else -> {
                createLabel("No selected project. ", gdxSettings.normalFontSize)
            }
        }

        val selectedProjectSignificanceLabel: Label = when {
            isBasicProjectSelected && !isAppliedProjectSelected -> {
                createLabel(
                    "Significance: %.2f".format(selectedBasicResearchProjectData.significance),
                    gdxSettings.normalFontSize
                )
            }
            isAppliedProjectSelected && !isBasicProjectSelected -> {
                createLabel(
                    "Significance: %.2f".format(selectedAppliedResearchProjectData.significance),
                    gdxSettings.normalFontSize
                )
            }
            isBasicProjectSelected && isAppliedProjectSelected -> {
                createLabel(
                    "",
                    gdxSettings.normalFontSize
                )
            }
            else -> {
                createLabel(
                    "",
                    gdxSettings.normalFontSize
                )
            }
        }

        knowledgeProjectTable.add(selectedKnowledgeMapDouble2D).pad(10f)

        knowledgeProjectTable.add(selectedProjectIdLabel).pad(20f)

        knowledgeProjectTable.add(selectedProjectSignificanceLabel).pad(20f)
    }

    private fun updateKnowledgeGroup() {
        knowledgeGroup.clear()

        knowledgeGroup.setSize(
            knowledgeMapWidthWithMargin().toFloat() * actualZoom(),
            knowledgeMapHeightWithMargin().toFloat() * actualZoom()
        )

        // Add done basic research project image
        playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach {
            val image: Image = createBasicProjectImage(it, true)

            logger.debug("Add basic research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        // Add done applied research project image
        playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach {
            val image: Image = createAppliedProjectImage(it, true)

            logger.debug("Add applied research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        // Add known basic research project image
        playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach {
            val image: Image = createBasicProjectImage(it, false)

            logger.debug("Add basic research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        // Add done applied research project image
        playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach {
            val image: Image = createAppliedProjectImage(it, false)

            logger.debug("Add applied research image to (${image.x}, ${image.y})")

            knowledgeGroup.addActor(image)
        }

        // Add done basic research project reference arrows
        playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach { projectData ->
            projectData.referenceBasicResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }

            projectData.referenceAppliedResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
        }

        // Add known basic research project reference arrows
        playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach { projectData ->
            projectData.referenceBasicResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }

            projectData.referenceAppliedResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
        }

        // Add known applied research project reference arrows
        playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach { projectData ->
            projectData.referenceBasicResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
                playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
            projectData.referenceAppliedResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
        }

        // Add known applied research project reference arrows
        playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach { projectData ->
            projectData.referenceBasicResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
                playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList.forEach {
                    if (it.basicResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
            projectData.referenceAppliedResearchIdList.forEach { id ->
                playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }

                playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList.forEach {
                    if (it.appliedResearchId == id) {
                        val image: Image = createReferenceArrow(it, projectData)
                        knowledgeGroup.addActor(image)
                    }
                }
            }
        }


        knowledgeGroup.addListener((object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(gdxSettings.soundEffectsVolume)

                game.universeClient.selectedKnowledgeDouble2D = convertKnowledgeGroupPosition(
                    Double2D(x.toDouble(), y.toDouble())
                )
            }
        }))

        knowledgeGroupScrollPane.actor = knowledgeGroup
    }

    /**
     * Create image of a basic research project
     *
     * @param project the basic research project data
     * @param isProjectDone is this a done project or a known project
     */
    private fun createBasicProjectImage(
        project: BasicResearchProjectData,
        isProjectDone: Boolean
    ): Image {

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
            a = if (isProjectDone) 1.0f else 0.5f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            selectedBasicResearchProjectData = project
            isBasicProjectSelected = true
            isAppliedProjectSelected = false
            updateKnowledgeProjectTable()
        }
    }


    /**
     * Create image of a applied research project
     *
     * @param project the basic research project data
     * @param isProjectDone is this a done project or a known project
     */
    private fun createAppliedProjectImage(
        project: AppliedResearchProjectData,
        isProjectDone: Boolean
    ): Image {

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
            a = if (isProjectDone) 1.0f else 0.5f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            selectedAppliedResearchProjectData = project
            isBasicProjectSelected = false
            isAppliedProjectSelected = true
            updateKnowledgeProjectTable()
        }
    }

    /**
     * Create arrow from basic research project to basic research project
     */
    private fun createReferenceArrow(
        from: BasicResearchProjectData,
        to: BasicResearchProjectData
    ): Image {
        val color: Color = Color.WHITE
        val fromDouble2D: Double2D = Double2D(
            (from.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (from.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        val toDouble2D: Double2D = Double2D(
            (to.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (to.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        return createArrow(
            from = fromDouble2D,
            to = toDouble2D,
            arrowWidth = 20.0f * gdxSettings.knowledgeMapProjectIconZoom,
            r = color.r,
            g = color.g,
            b = color.b,
            a = color.a,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {}
    }

    /**
     * Create arrow from basic research project to applied research project
     */
    private fun createReferenceArrow(
        from: BasicResearchProjectData,
        to: AppliedResearchProjectData
    ): Image {
        val color: Color = Color.WHITE
        val fromDouble2D: Double2D = Double2D(
            (from.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (from.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        val toDouble2D: Double2D = Double2D(
            (to.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (to.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )

        return createArrow(
            from = fromDouble2D,
            to = toDouble2D,
            arrowWidth = 20.0f * gdxSettings.knowledgeMapProjectIconZoom,
            r = color.r,
            g = color.g,
            b = color.b,
            a = color.a,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {}
    }

    /**
     * Create arrow from applied research project to basic research project
     */
    private fun createReferenceArrow(
        from: AppliedResearchProjectData,
        to: BasicResearchProjectData
    ): Image {
        val color: Color = Color.WHITE
        val fromDouble2D: Double2D = Double2D(
            (from.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (from.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        val toDouble2D: Double2D = Double2D(
            (to.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (to.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        return createArrow(
            from = fromDouble2D,
            to = toDouble2D,
            arrowWidth = 20.0f * gdxSettings.knowledgeMapProjectIconZoom,
            r = color.r,
            g = color.g,
            b = color.b,
            a = color.a,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {}
    }

    /**
     * Create arrow from applied research project to applied research project
     */
    private fun createReferenceArrow(
        from: AppliedResearchProjectData,
        to: AppliedResearchProjectData
    ): Image {
        val color: Color = Color.WHITE
        val fromDouble2D: Double2D = Double2D(
            (from.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (from.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        val toDouble2D: Double2D = Double2D(
            (to.xCor - knowledgeMapMinX() + knowledgeMapMargin()) * actualZoom(),
            (to.yCor - knowledgeMapMinY() + knowledgeMapMargin()) * actualZoom()
        )
        return createArrow(
            from = fromDouble2D,
            to = toDouble2D,
            arrowWidth = 20.0f * gdxSettings.knowledgeMapProjectIconZoom,
            r = color.r,
            g = color.g,
            b = color.b,
            a = color.a,
            soundVolume = gdxSettings.soundEffectsVolume,
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
        val allBasicProject =
            playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList
        val allAppliedProject =
            playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList
        val minBasicX: Double = allBasicProject.minOfOrNull {
            it.xCor
        } ?: -1.0
        val minAppliedX: Double = allAppliedProject.minOfOrNull {
            it.xCor
        } ?: -1.0

        return min(minBasicX, minAppliedX)
    }

    /**
     * Max x coordinate of the knowledge map
     */
    private fun knowledgeMapMaxX(): Double {
        // Compute the dimension of knowledge map
        val allBasicProject =
            playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList
        val allAppliedProject =
            playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList
        val maxBasicX: Double = allBasicProject.maxOfOrNull {
            it.xCor
        } ?: 1.0
        val maxAppliedX: Double = allAppliedProject.maxOfOrNull {
            it.xCor
        } ?: 1.0

        return max(maxBasicX, maxAppliedX)
    }

    /**
     * Min y coordinate of the knowledge map
     */
    private fun knowledgeMapMinY(): Double {
        // Compute the dimension of knowledge map
        val allBasicProject =
            playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList
        val allAppliedProject =
            playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList
        val minBasicY: Double = allBasicProject.minOfOrNull {
            it.yCor
        } ?: -1.0
        val minAppliedY: Double = allAppliedProject.minOfOrNull {
            it.yCor
        } ?: -1.0

        return min(minBasicY, minAppliedY)
    }


    /**
     * Max y coordinate of the knowledge map
     */
    private fun knowledgeMapMaxY(): Double {
        // Compute the dimension of knowledge map
        val allBasicProject =
            playerData.playerInternalData.playerScienceData().doneBasicResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownBasicResearchProjectList
        val allAppliedProject =
            playerData.playerInternalData.playerScienceData().doneAppliedResearchProjectList +
                    playerData.playerInternalData.playerScienceData().knownAppliedResearchProjectList
        val maxBasicY: Double = allBasicProject.maxOfOrNull {
            it.yCor
        } ?: 1.0
        val maxAppliedY: Double = allAppliedProject.maxOfOrNull {
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
    private fun knowledgeMapWidthWithMargin(): Double =
        knowledgeMapWidth() + 2 * knowledgeMapMargin()

    /**
     * The knowledge map height with margin
     */
    private fun knowledgeMapHeightWithMargin(): Double =
        knowledgeMapHeight() + 2 * knowledgeMapMargin()


    /**
     * Translate knowledgeMapZoomRelativeToFullMap to actual zoom
     */
    private fun actualZoom(): Float {
        // Actual zoom when mapZoomRelativeToFullMap equals 1.0
        val zoomOne = min(
            knowledgeGroupScrollPane.width / knowledgeMapWidthWithMargin().toFloat(),
            knowledgeGroupScrollPane.height / knowledgeMapHeightWithMargin().toFloat()
        )
        return zoomOne * gdxSettings.knowledgeMapZoomRelativeToFullMap
    }

    /**
     * Translate position of the knowledge group to position in knowledge map
     */
    private fun convertKnowledgeGroupPosition(pos: Double2D): Double2D {
        val x: Double = pos.x / actualZoom() + knowledgeMapMinX() - knowledgeMapMargin()
        val y: Double = pos.y / actualZoom() + knowledgeMapMinY() - knowledgeMapMargin()

        return Double2D(x, y)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}