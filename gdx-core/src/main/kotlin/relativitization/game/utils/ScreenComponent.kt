package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField

abstract class ScreenComponent<out T : Actor>(val assets: Assets){
    val skin: Skin = assets.getSkin()

    abstract fun get(): T

    /**
     * Create scroll pane for table
     *
     * @param table the table to add scroll pane
     */
    fun createScrollPane(table: Table): ScrollPane = ActorFunction.createScrollPane(skin, table)

    /**
     * Create label to display text
     *
     * @param text text to display
     * @param fontSize size of the font
     */
    fun createLabel(text: String, fontSize: Int = 16): Label = ActorFunction.createLabel(skin, assets, text, fontSize)

    /**
     * Create a text button
     *
     * @param text the text in the button
     * @param fontSize size of the font
     * @param function the function called when clicking this button, take this button as parameter
     */
    fun createTextButton(
        text: String,
        fontSize: Int = 30,
        function: (TextButton) -> Unit = {},
    ): TextButton = ActorFunction.createTextButton(skin, assets, text, fontSize, function)

    /**
     * Create check box
     *
     * @param text Description of the option
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    fun createCheckBox(
        text: String,
        default: Boolean,
        fontSize: Int = 16,
        function: (Boolean, CheckBox) -> Unit = { _, _ -> }
    ): CheckBox = ActorFunction.createCheckBox(skin, assets, text, default, fontSize, function)

    /**
     * Create select box
     *
     * @param itemList the list of item to be selected
     * @param default the default value of the select box
     * @param fontSize the font size of the select box
     * @param function the function acted after the select box is changed, take this select box as parameter
     */
    inline fun <reified T> createSelectBox(
        itemList: List<T>,
        default: T = itemList[0],
        fontSize: Int = 16,
        crossinline function: (T, SelectBox<T>) -> Unit = { _, _ -> },
    ): SelectBox<T> = ActorFunction.createSelectBox(skin, assets, itemList, default, fontSize, function)


    /**
     * Create text field
     *
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this text field as parameter
     */
    fun createTextField(
        default: String,
        fontSize: Int = 16,
        function: (String, TextField) -> Unit = { _, _ -> }
    ): TextField = ActorFunction.createTextField(skin, assets, default, fontSize, function)

    fun disableActor(actor: Actor) = ActorFunction.disableActor(actor)
}