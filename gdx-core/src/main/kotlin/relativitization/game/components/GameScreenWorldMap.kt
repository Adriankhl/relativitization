package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage.getPlayerImages
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.physics.Int3D
import relativitization.universe.maths.grid.Data3D2DProjection
import relativitization.universe.maths.grid.Projections.createData3D2DProjection
import kotlin.math.abs
import kotlin.math.min

class GameScreenWorldMap(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting

    private val group: Group = Group()
    private val scrollPane: ScrollPane = createScrollPane(group)
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

    override fun getActor(): ScrollPane {
        return scrollPane
    }

    override fun onGdxSettingsChange() {
        // Update zoom factor if the difference is great enough
        if ((abs(actualZoom() - oldActualZoom) / oldActualZoom) > 0.1f) {
            val oldScrollX = scrollPane.scrollX
            val oldScrollY = scrollPane.scrollY
            updateGroup()
            scrollPane.scrollX = oldScrollX * (actualZoom() / oldActualZoom)
            scrollPane.scrollY = oldScrollY * (actualZoom() / oldActualZoom)

            oldActualZoom = actualZoom()

            scrollPane.updateVisualScroll()
        }
    }

    override fun onUniverseData3DChange() {
        data3D2DProjection = update3D2DProjection()
        updateGroup()
    }

    override fun onUniverseDataViewChange() {
        data3D2DProjection = update3D2DProjection()
        updateGroup()
    }

    override fun onPrimarySelectedInt3DChange() {
        drawSelected()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        drawSelected()
    }

    override fun onSelectedPlayerIdListChange() {
        drawSelected()
    }

    private fun clear() {
        group.clear()
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
            gridXSeparation = 256,
            gridYSeparation = 128,
            xPadding = 1024,
            yPadding = 1024,
        )
    }

    private fun actualZoom(): Float {
        val oneZoom = min(scrollPane.width / data3D2DProjection.width, scrollPane.height / data3D2DProjection.height)
        return oneZoom * gdxSetting.mapZoomRelativeToFullMap
    }

    private fun updateGroup() {
        clear()
        group.setSize(
            data3D2DProjection.width.toFloat() * actualZoom(),
            data3D2DProjection.height.toFloat() * actualZoom()
        )
        for (x in data3D2DProjection.xBegin..data3D2DProjection.xEnd) {
            for (y in data3D2DProjection.yBegin..data3D2DProjection.yEnd) {
                for (z in data3D2DProjection.zBegin..data3D2DProjection.zEnd) {
                    val gridRectangle = data3D2DProjection.int3DToRectangle(Int3D(x, y, z))
                    val image = createImage(
                        "basic/white-pixel",
                        gridRectangle.xPos.toFloat() * actualZoom(),
                        gridRectangle.yPos.toFloat() * actualZoom(),
                        gridRectangle.width.toFloat() * actualZoom(),
                        gridRectangle.height.toFloat() * actualZoom(),
                        1.0f,
                        1.0f,
                        1.0f,
                        0.4f,
                        gdxSetting.soundEffectsVolume
                    ) {
                        game.universeClient.primarySelectedInt3D = Int3D(x, y, z)
                    }
                    group.addActor(image)
                    int3DActorMap[Int3D(x, y, z)] = image
                }
            }
        }


        for (id in data3D2DProjection.idList) {
            val attachedId: Int = game.universeClient.getUniverseData3D().get(id).attachedPlayerId
            val int3D: Int3D = game.universeClient.getUniverseData3D().get(id).int4D.toInt3D()
            val playerRectangle = data3D2DProjection.data3DToRectangle(int3D, attachedId, id)
            println("player rectangle: $playerRectangle")

            val images = getPlayerImages(
                game.universeClient.getUniverseData3D().get(id),
                assets,
                playerRectangle.xPos.toFloat() * actualZoom(),
                playerRectangle.yPos.toFloat() * actualZoom(),
                playerRectangle.width.toFloat() * actualZoom(),
                playerRectangle.height.toFloat() * actualZoom(),
                gdxSetting.soundEffectsVolume,
            ) {
                game.universeClient.newSelectedPlayerId = id
            }

            images.forEach { group.addActor(it) }
            playerSquareActorMap[id] = images.last()
        }

        scrollPane.actor = group

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
                gdxSetting.soundEffectsVolume
            )
            group.addActorBefore(image, square)
            selectSquare[int3D] = square
        }

        for (id in game.universeClient.allSelectedPlayerIdList) {
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
                        gdxSetting.soundEffectsVolume
                    )
                    group.addActorBefore(image, circle)
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
                        gdxSetting.soundEffectsVolume
                    )
                    group.addActorBefore(image, circle)
                    selectCircle[id] = circle
                }
            }
        }
    }

    /**
     * Clear selected int3d
     */
    private fun clearAllSelectedInt3D() {
        selectSquare.forEach { group.removeActor(it.value) }
        selectSquare.clear()
    }


    /**
     * Clear all selected player
     */
    private fun clearAllSelectedPlayer() {
        selectCircle.forEach { group.removeActor(it.value) }
        selectCircle.clear()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}