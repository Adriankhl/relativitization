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
import kotlin.math.min

class GameScreenWorldMap(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting

    lateinit var updateInfo: () -> Unit

    private val group: Group = Group()
    private val scrollPane: ScrollPane = createScrollPane(group)
    private var data3D2DProjection: Data3D2DProjection = update3D2DProjection()
    private var zoom: Float = 1.0f

    private val selectCircle: MutableMap<Int, Actor> = mutableMapOf()
    private val selectSquare: MutableMap<Int3D, Actor> = mutableMapOf()

    private val playerSquareActorMap: MutableMap<Int, Actor> = mutableMapOf()
    private val int3DActorMap: MutableMap<Int3D, Actor> = mutableMapOf()

    init {
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)
        updateGroup()
    }

    override fun get(): ScrollPane {
        return scrollPane
    }

    override fun update() {
        data3D2DProjection = update3D2DProjection()
        updateGroup()
    }

    fun clear() {
        group.clear()
        selectSquare.clear()
        selectCircle.clear()
        playerSquareActorMap.clear()
        int3DActorMap.clear()
    }

    fun update3D2DProjection(): Data3D2DProjection {
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

    fun updateGroup() {
        clear()
        group.setSize(data3D2DProjection.width.toFloat() * zoom, data3D2DProjection.height.toFloat() * zoom)
        for (x in data3D2DProjection.xBegin..data3D2DProjection.xEnd) {
            for (y in data3D2DProjection.yBegin..data3D2DProjection.yEnd) {
                for (z in data3D2DProjection.zBegin..data3D2DProjection.zEnd) {
                    val gridRectangle = data3D2DProjection.int3DToRectangle(Int3D(x, y, z))
                    val image = createImage(
                        "basic/white-pixel",
                        gridRectangle.xPos.toFloat() * zoom,
                        gridRectangle.yPos.toFloat() * zoom,
                        gridRectangle.width.toFloat() * zoom,
                        gridRectangle.height.toFloat() * zoom,
                        1.0f,
                        1.0f,
                        1.0f,
                        0.4f,
                        gdxSetting.soundEffectsVolume
                    ) {
                        selectInt3D(Int3D(x, y, z), it)
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
                playerRectangle.xPos.toFloat() * zoom,
                playerRectangle.yPos.toFloat() * zoom,
                playerRectangle.width.toFloat() * zoom,
                playerRectangle.height.toFloat() * zoom,
                gdxSetting.soundEffectsVolume,
            ) {
                selectPlayer(id, it)
            }

            images.forEach { group.addActor(it) }
            playerSquareActorMap[id] = images.last()
        }

        scrollPane.actor = group

        drawSelected()
    }

    fun zoomIn() {
        val oldScrollX = scrollPane.scrollX
        val oldScrollY = scrollPane.scrollY
        zoom *= gdxSetting.zoomFactor
        updateGroup()
        scrollPane.scrollX = oldScrollX * gdxSetting.zoomFactor
        scrollPane.scrollY = oldScrollY * gdxSetting.zoomFactor
        scrollPane.updateVisualScroll()
    }

    fun zoomOut() {
        val oldScrollX = scrollPane.scrollX
        val oldScrollY = scrollPane.scrollY
        zoom /= gdxSetting.zoomFactor
        updateGroup()
        scrollPane.scrollX = oldScrollX / gdxSetting.zoomFactor
        scrollPane.scrollY = oldScrollY / gdxSetting.zoomFactor
        scrollPane.updateVisualScroll()
    }

    fun zoomToFullMap() {
        zoom = min(scrollPane.width / data3D2DProjection.width, scrollPane.height / data3D2DProjection.height)
        updateGroup()
    }

    /**
     * Draw selected int3D and player
     */
    fun drawSelected() {
        selectSquare.clear()
        selectCircle.clear()
        for (id in game.universeClient.selectedPlayerIds) {
            if (playerSquareActorMap.containsKey(id)) {
                if (id == game.universeClient.firstSelectedPlayerId) {
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
                }
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

        for (int3D in game.universeClient.selectedInt3Ds) {
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
        }
    }

    /**
     * Select int3d (grid) by adding a square boundary
     */
    fun selectInt3D(int3D: Int3D, image: Image) {
        if (game.universeClient.selectedInt3Ds.isEmpty()) {
            game.universeClient.selectedInt3Ds.add(int3D)
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
        } else {
            // clear and select new int3D if no already selected
            if (!selectSquare.containsKey(int3D)) {
                clearAllSelectedInt3D()

                game.universeClient.selectedInt3Ds.add(int3D)
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
            } else {
                clearAllSelectedInt3D()
            }
        }
    }

    /**
     * Clear selected int3d
     */
    fun clearAllSelectedInt3D() {
        game.universeClient.selectedInt3Ds.clear()
        selectSquare.forEach { group.removeActor(it.value)  }
        selectSquare.clear()
    }

    /**
     * Select player by adding a circle on top of the player
     */
    fun selectPlayer(id: Int, image: Image) {
        // change the first selected player id if no stored selected player or first selected not stored
        // for selecting first player then select other without changing the first selected player
        if (game.universeClient.selectedPlayerIds.isEmpty() ||
            (!game.universeClient.selectedPlayerIds.contains(game.universeClient.firstSelectedPlayerId) &&
             !game.universeClient.selectedPlayerIds.contains(id))
        ) {
            game.universeClient.firstSelectedPlayerId = id
            game.universeClient.selectedPlayerIds.add(id)
            // add green circle
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
        } else if (!game.universeClient.selectedPlayerIds.contains(id)) {
            game.universeClient.selectedPlayerIds.add(id)
            // add red circle
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
        } else {
            clearSelectedPlayer(id)
        }
    }

    /**
     * Clear selected player
     */
    fun clearSelectedPlayer(id: Int) {
        game.universeClient.selectedPlayerIds.remove(id)
        if (selectCircle.containsKey(id)) {
            group.removeActor(selectCircle[id])
            selectCircle.remove(id)
        }
    }

    /**
     * Clear all selected player
     */
    fun clearAllSelectedPlayer() {
        game.universeClient.selectedPlayerIds.clear()
        selectCircle.forEach {group.removeActor(it.value)}
        selectCircle.clear()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}