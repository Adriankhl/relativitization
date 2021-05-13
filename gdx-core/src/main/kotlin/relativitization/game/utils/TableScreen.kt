package relativitization.game.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport

open class TableScreen(assets: Assets) : ScreenAdapter() {
    private val skin: Skin = assets.getSkin()

    protected val stage: Stage = Stage(ScreenViewport())
    protected val root: Table = Table()

    init {
        // Modify label color to white
        skin.get(LabelStyle::class.java).fontColor = Color.WHITE
    }

    override fun show() {
        root.setFillParent(true);
        stage.addActor(root)
        Gdx.input.setInputProcessor(stage)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }


    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        stage.clear()
    }

    override fun dispose() {
        skin.dispose()
        stage.dispose()
    }

    /**
     * Create a text button
     *
     * @param text the text in the button
     * @param fontScale scaling of the font, the label should be managed by the cell
     * @param function the function called when clicking this button
     */
    fun createTextButton(
        text: String,
        fontScale: Float = -1.0f,
        function: () -> Unit = {},
    ): TextButton {

        val button = TextButton(text, skin)

        if (fontScale > 0.0f) {
            button.label.setFontScale(fontScale)
        }

        button.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                function()
            }
        })

        return button
    }

    fun createLabel(text: String): Label {
        val labelStyle = skin.get(LabelStyle::class.java)
        labelStyle.fontColor = Color.WHITE
        return Label(text, labelStyle)
    }
}