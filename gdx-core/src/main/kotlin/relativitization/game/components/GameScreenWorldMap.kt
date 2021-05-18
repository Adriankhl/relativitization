package relativitization.game.components

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.physics.Int3D
import relativitization.universe.maths.grid.Data3D2DProjection
import relativitization.universe.maths.grid.Projections.createData3D2DProjection

class GameScreenWorldMap(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val group: Group = Group()
    private val scrollPane: ScrollPane = createScrollPane(group)
    private var data3D2DProjection: Data3D2DProjection = update3D2DProjection()
    private var zoom: Float = 1.0f

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
    }

    fun clear() {
        group.clear()
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
                    group.addActor(
                        assets.getImage(
                            "background/white-pixel",
                            gridRectangle.xPos.toFloat() * zoom,
                            gridRectangle.yPos.toFloat() * zoom,
                            gridRectangle.width.toFloat() * zoom,
                            gridRectangle.height.toFloat() * zoom,
                            1.0f,
                            1.0f,
                            1.0f,
                            0.4f,
                        )
                    )
                }
            }
        }


        for (id in data3D2DProjection.idList) {
            val attachedId: Int = game.universeClient.getUniverseData3D().get(id).attachedPlayerId
            val int3D: Int3D = game.universeClient.getUniverseData3D().get(id).int4D.toInt3D()
            val playerRectangle = data3D2DProjection.data3DToRectangle(int3D, attachedId, id)
            println("player rectangle: $playerRectangle")
            group.addActor(
                assets.getImage(
                    id,
                    "system/ship1",
                    playerRectangle.xPos.toFloat() * zoom,
                    playerRectangle.yPos.toFloat() * zoom,
                    playerRectangle.width.toFloat() * zoom,
                    playerRectangle.height.toFloat() * zoom,
                )
            )
        }

        scrollPane.actor = group
    }

    fun zoomIn() {
        zoom *= 1.1f
        updateGroup()
    }

    fun zoomOut() {
        zoom /= 1.1f
        updateGroup()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}