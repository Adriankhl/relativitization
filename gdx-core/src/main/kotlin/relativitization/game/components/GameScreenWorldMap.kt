package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.maths.grid.Data3D2DProjection
import relativitization.universe.maths.grid.Projections.createData3D2DProjection

class GameScreenWorldMap(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val group: Group = Group()
    private var data3D2DProjection: Data3D2DProjection = update3D2DProjection()

    override fun get(): ScrollPane {
        val scrollPane: ScrollPane = createScrollPane(group)
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(false)
        return scrollPane
    }

    override fun update() {
        data3D2DProjection = update3D2DProjection()
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
}