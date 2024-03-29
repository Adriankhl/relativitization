package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage.getPlayerImages
import relativitization.game.utils.ScreenComponent
import relativitization.universe.core.maths.grid.Data3D2DProjection
import relativitization.universe.core.maths.grid.Projections.createData3D2DProjection
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.math.abs
import kotlin.math.min

class GameScreenWorldMap(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private val allImageGroup: Group = Group()
    private val scrollPane: ScrollPane = createScrollPane(allImageGroup, this::onGdxSettingsChange)
    private var data3D2DProjection: Data3D2DProjection = update3D2DProjection()

    private var oldActualZoom: Float = 1.0f

    private val selectCircle: MutableMap<Int, Actor> = mutableMapOf()
    private val selectSquare: MutableMap<Int3D, Actor> = mutableMapOf()

    private val playerSquareActorMap: MutableMap<Int, Actor> = mutableMapOf()
    private val int3DActorMap: MutableMap<Int3D, Actor> = mutableMapOf()

    init {
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)
        updateGroup()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onGdxSettingsChange() {
        // Update zoom factor if the difference is great enough
        if ((abs(actualZoom() - oldActualZoom) / oldActualZoom) > gdxSettings.mapZoomFactor / 10) {
            val oldScrollX = scrollPane.scrollX
            val oldScrollY = scrollPane.scrollY

            logger.debug("zoom: old x = $oldScrollX, old y = $oldScrollY")

            updateGroup()

            // Call layout() to modify maxX and maxY
            scrollPane.layout()

            val zoomRatio = actualZoom() / oldActualZoom

            // oldScrollX * zoomRatio: scroll to the correct lower left corner
            // scrollPane.scrollWidth * (zoomRatio - 1.0f) / 2: scroll a bit more/less to compensate the
            // moved center due to the scroll
            scrollPane.scrollX =
                oldScrollX * zoomRatio + scrollPane.scrollWidth * (zoomRatio - 1.0f) / 2
            scrollPane.scrollY =
                oldScrollY * zoomRatio + scrollPane.scrollHeight * (zoomRatio - 1.0f) / 2

            logger.debug("zoom: new x = ${scrollPane.scrollX}, new y = ${scrollPane.scrollY}")

            oldActualZoom = actualZoom()

            scrollPane.updateVisualScroll()
        } else {
            // Update to change the map, e.g., color
            updateGroup()
        }
    }

    override fun onUniverseData3DChange() {
        data3D2DProjection = update3D2DProjection()
        updateGroup()

        // The new universe data 3D may have different height and width
        oldActualZoom = actualZoom()
    }

    override fun onUniverseDataViewChange() {
        data3D2DProjection = update3D2DProjection()
        updateGroup()

        // The new universe data 3D may have different height and width
        oldActualZoom = actualZoom()
    }

    override fun onPrimarySelectedInt3DChange() {
        drawSelected()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updateGroup()
        drawSelected()
    }

    override fun onSelectedPlayerIdListChange() {
        drawSelected()
    }

    override fun onMapCenterPlayerIdChange() {
        scrollTo(game.universeClient.mapCenterPlayerId)
    }

    private fun clear() {
        allImageGroup.clear()
        selectSquare.clear()
        selectCircle.clear()
        playerSquareActorMap.clear()
        int3DActorMap.clear()
    }

    private fun update3D2DProjection(): Data3D2DProjection {
        return createData3D2DProjection(
            data3D = game.universeClient.getUniverseData3D().playerId3DMap,
            center = game.universeClient.universeClientSettings.viewCenter.toInt3D(),
            zLimit = game.universeClient.universeClientSettings.zLimit,
            imageWidth = 512,
            imageHeight = 512,
            gridXSeparation = 0,
            gridYSeparation = 0,
            extraGridSeparation = 256,
            xPadding = 512,
            yPadding = 512,
        )
    }

    private fun actualZoom(): Float {
        // Actual zoom when mapZoomRelativeToFullMap equals 1.0
        val zoomOne = min(
            scrollPane.width / data3D2DProjection.width,
            scrollPane.height / data3D2DProjection.height
        )
        return zoomOne * gdxSettings.mapZoomRelativeToFullMap
    }

    private fun updateGroup() {
        clear()
        allImageGroup.setSize(
            data3D2DProjection.width.toFloat() * actualZoom(),
            data3D2DProjection.height.toFloat() * actualZoom()
        )
        for (x in data3D2DProjection.xBegin..data3D2DProjection.xEnd) {
            for (y in data3D2DProjection.yBegin..data3D2DProjection.yEnd) {
                for (z in data3D2DProjection.zBegin..data3D2DProjection.zEnd) {
                    val gridRectangle = data3D2DProjection.int3DToRectangle(Int3D(x, y, z))
                    val image = createImage(
                        name = "basic/white-pixel",
                        xPos = gridRectangle.xPos.toFloat() * actualZoom(),
                        yPos = gridRectangle.yPos.toFloat() * actualZoom(),
                        width = gridRectangle.width.toFloat() * actualZoom(),
                        height = gridRectangle.height.toFloat() * actualZoom(),
                        r = 1.0f,
                        g = 1.0f,
                        b = 1.0f,
                        a = 0.4f,
                        soundVolume = gdxSettings.soundEffectsVolume
                    ) {
                        game.universeClient.primarySelectedInt3D = Int3D(x, y, z)
                    }
                    allImageGroup.addActor(image)
                    int3DActorMap[Int3D(x, y, z)] = image
                }
            }
        }


        for (id in data3D2DProjection.idList) {
            val groupId: Int = game.universeClient.getUniverseData3D().get(id).groupId
            val int3D: Int3D = game.universeClient.getUniverseData3D().get(id).int4D.toInt3D()
            val playerRectangle = data3D2DProjection.data3DToRectangle(int3D, groupId, id)

            logger.debug("player rectangle: $playerRectangle")

            val images = getPlayerImages(
                playerData = game.universeClient.getUniverseData3D().get(id),
                universeData3DAtPlayer = game.universeClient.getUniverseData3D(),
                primaryPlayerData = game.universeClient.getPrimarySelectedPlayerData(),
                assets = assets,
                xPos = playerRectangle.xPos.toFloat() * actualZoom(),
                yPos = playerRectangle.yPos.toFloat() * actualZoom(),
                width = playerRectangle.width.toFloat() * actualZoom(),
                height = playerRectangle.height.toFloat() * actualZoom(),
                soundVolume = gdxSettings.soundEffectsVolume,
                mapPlayerColorMode = gdxSettings.mapPlayerColorMode,
                showCombat = true,
            ) {
                game.universeClient.newSelectedPlayerId = id
            }

            images.forEach { allImageGroup.addActor(it) }
            playerSquareActorMap[id] = images.last()
        }

        scrollPane.actor = allImageGroup

        drawSelected()
    }


    /**
     * Draw selected int3D and player
     */
    private fun drawSelected() {
        // Clear all then redraw
        clearAllSelectedInt3D()
        clearAllSelectedPlayer()

        val int3D = game.universeClient.primarySelectedInt3D
        if (int3DActorMap.containsKey(int3D)) {
            val image = int3DActorMap.getValue(int3D)
            val square = createImage(
                "basic/white-square-boundary",
                image.x,
                image.y,
                image.width,
                image.height,
                0.0f,
                0.0f,
                1.0f,
                1.0f,
                gdxSettings.soundEffectsVolume
            )
            allImageGroup.addActorBefore(image, square)
            selectSquare[int3D] = square
        }

        for (id in game.universeClient.selectedPlayerIdList) {
            if (playerSquareActorMap.containsKey(id)) {
                if (id == game.universeClient.primarySelectedPlayerId) {
                    val image = playerSquareActorMap.getValue(id)
                    val circle = createImage(
                        "basic/white-ring",
                        image.x,
                        image.y,
                        image.width,
                        image.height,
                        0.0f,
                        1.0f,
                        0.0f,
                        1.0f,
                        gdxSettings.soundEffectsVolume
                    )
                    allImageGroup.addActorBefore(image, circle)
                    selectCircle[id] = circle
                } else {
                    val image = playerSquareActorMap.getValue(id)
                    val circle = createImage(
                        "basic/white-ring",
                        image.x,
                        image.y,
                        image.width,
                        image.height,
                        1.0f,
                        0.0f,
                        0.0f,
                        1.0f,
                        gdxSettings.soundEffectsVolume
                    )
                    allImageGroup.addActorBefore(image, circle)
                    selectCircle[id] = circle
                }
            }
        }
    }

    /**
     * Clear selected int3d
     */
    private fun clearAllSelectedInt3D() {
        selectSquare.forEach { allImageGroup.removeActor(it.value) }
        selectSquare.clear()
    }


    /**
     * Clear all selected player
     */
    private fun clearAllSelectedPlayer() {
        selectCircle.forEach { allImageGroup.removeActor(it.value) }
        selectCircle.clear()
    }

    /**
     * Scroll to center player
     */
    private fun scrollTo(id: Int) {
        if (playerSquareActorMap.keys.contains(id)) {
            val image = playerSquareActorMap.getValue(id)
            val imageCenterX: Float = image.getX(Align.center)
            val imageCenterY: Float = image.getY(Align.center)
            scrollPane.scrollX = imageCenterX - scrollPane.scrollWidth / 2
            // The y position of scroll bar is inverse to that in the projected coordinate
            scrollPane.scrollY = allImageGroup.height - imageCenterY - scrollPane.scrollHeight / 2
            logger.debug("scroll to: x = ${scrollPane.scrollX}, y = ${scrollPane.scrollY}")
        } else {
            logger.debug("Scroll fail, no player id: $id in world map")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}