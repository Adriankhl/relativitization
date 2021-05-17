package relativitization.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array

object ActorFunction {
    /**
     * Create scroll pane for table
     *
     * @param table the table to add scroll pane
     */
    fun createScrollPane(skin: Skin, table: Table): ScrollPane = ScrollPane(table, skin)

    /**
     * Create label to display text
     *
     * @param text text to display
     * @param fontSize size of the font
     */
    fun createLabel(skin: Skin, assets: Assets, text: String, fontSize: Int = 16): Label {
        val style = skin.get(Label.LabelStyle::class.java)
        style.font = assets.getFont(fontSize)
        style.fontColor = Color.WHITE

        return Label(text, style)
    }

    /**
     * Create a text button
     *
     * @param text the text in the button
     * @param fontSize size of the font
     * @param function the function called when clicking this button, take this button as parameter
     */
    fun createTextButton(
        skin: Skin,
        assets: Assets,
        text: String,
        fontSize: Int = 30,
        soundVolume: Float = 0.5f,
        function: (TextButton) -> Unit = {},
    ): TextButton {

        val style = skin.get(TextButton.TextButtonStyle::class.java)

        style.font = assets.getFont(fontSize)

        val button = TextButton(text, style)

        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(button)
            }
        })

        return button
    }

    /**
     * Create check box
     *
     * @param text Description of the option
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    fun createCheckBox(
        skin: Skin,
        assets: Assets,
        text: String,
        default: Boolean,
        fontSize: Int = 16,
        function: (Boolean, CheckBox) -> Unit = { _, _ -> }
    ): CheckBox {
        val style = skin.get(CheckBox.CheckBoxStyle::class.java)
        style.font = assets.getFont(fontSize)

        val checkBox = CheckBox(text, style)

        checkBox.isChecked = default

        checkBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                function(checkBox.isChecked, checkBox)
            }
        })

        return checkBox
    }

    /**
     * Create select box
     *
     * @param itemList the list of item to be selected
     * @param default the default value of the select box
     * @param fontSize the font size of the select box
     * @param function the function acted after the select box is changed, take this select box as parameter
     */
    inline fun <reified T> createSelectBox(
        skin: Skin,
        assets: Assets,
        itemList: List<T>,
        default: T = itemList[0],
        fontSize: Int = 16,
        crossinline function: (T, SelectBox<T>) -> Unit = { _, _ -> },
    ): SelectBox<T> {
        val style = skin.get(SelectBox.SelectBoxStyle::class.java)
        style.font = assets.getFont(fontSize)
        style.listStyle.font = assets.getFont(fontSize)

        val selectBox: SelectBox<T> = SelectBox(style)
        selectBox.items = Array(itemList.toTypedArray())

        selectBox.selected = default

        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                function(selectBox.selected, selectBox)
            }
        })
        return selectBox
    }

    /**
     * Create text field
     *
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this text field as parameter
     */
    fun createTextField(
        skin: Skin,
        assets: Assets,
        default: String,
        fontSize: Int = 16,
        function: (String, TextField) -> Unit = { _, _ -> }
    ): TextField {
        val style = skin.get(TextField.TextFieldStyle::class.java)
        style.font = assets.getFont(fontSize)

        val textField = TextField(default, style)

        textField.setTextFieldListener { field, _ -> function(field?.text ?: "", field!!) }

        return textField
    }


    fun disableActor(actor: Actor) {
        actor.touchable = Touchable.disabled
        actor.color = Color.GRAY
    }
}