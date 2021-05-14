package relativitization.game.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport

open class TableScreen(val assets: Assets) : ScreenAdapter() {
    val skin: Skin = assets.getSkin()

    protected val stage: Stage = Stage(ScreenViewport())
    protected val root: Table = Table()


    override fun show() {
        root.setFillParent(true)
        stage.addActor(root)
        Gdx.input.inputProcessor = stage
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
        stage.dispose()
    }

    /**
     * Create a text button
     *
     * @param text the text in the button
     * @param fontSize size of the font
     * @param function the function called when clicking this button
     */
    fun createTextButton(
        text: String,
        fontSize: Int = 30,
        function: () -> Unit = {},
    ): TextButton {

        val style = skin.get(TextButtonStyle::class.java)

        style.font = assets.getFont(fontSize)

        val button = TextButton(text, style)

        button.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                function()
            }
        })

        return button
    }

    fun createLabel(text: String, fontSize: Int = 16): Label {
        val style = skin.get(LabelStyle::class.java)
        style.font = assets.getFont(fontSize)
        style.fontColor = Color.WHITE

        return Label(text, style)
    }

    fun createScrollPane(table: Table): ScrollPane = ScrollPane(table, skin)

    inline fun <reified T> createSelectBox(
        itemList: List<T>,
        default: T = itemList[0],
        fontSize: Int = 16,
        crossinline function: (T) -> Unit = {},
    ): SelectBox<T> {
        val style = skin.get(SelectBoxStyle::class.java)
        style.font = assets.getFont(fontSize)

        val selectBox: SelectBox<T> = SelectBox(style)
        selectBox.items = Array(itemList.toTypedArray())

        selectBox.selected = default

        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                function(selectBox.selected)
            }
        })
        return selectBox
    }
}