package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame

abstract class ScreenComponent<out T : Actor>(val assets: Assets){
    val skin: Skin = assets.getSkin()

    private val allChildScreenComponentList: MutableList<ScreenComponent<Actor>> = mutableListOf()

    /**
     * Get the actor (e.g. table, group) of this component
     */
    abstract fun getActor(): T

    /**
     * Add all components of a child component to this
     *
     * @param component a child component
     */
    fun addChildScreenComponent(component: ScreenComponent<Actor>) {
        allChildScreenComponentList.addAll(component.getScreenComponentList())
    }

    /**
     * Get all screen component, including self and child components
     */
    fun getScreenComponentList(): List<ScreenComponent<Actor>> {
        return allChildScreenComponentList + listOf(this)
    }

    /**
     * Call this function when gdx setting change
     */
    open fun onGdxSettingsChange() {}

    /**
     * Call this function at each iteration of universe client
     */
    open fun onServerStatusChange() {}

    /**
     * Call this function when switching to new universeData
     */
    open fun onUniverseData3DChange() {}

    /**
     * Call this function when changing view int3D and z limit
     */
    open fun onUniverseDataViewChange() {}

    /**
     * Call this function when changing first selected id
     */
    open fun onPrimarySelectedPlayerIdChange() {}

    /**
     * Call this function when changing selectedPlayerId
     */
    open fun onSelectedPlayerIdListChange() {}

    /**
     * Call this function when selecting different primary int3D
     */
    open fun onPrimarySelectedInt3DChange() {}

    /**
     * Call this function when command to be confirm change
     */
    open fun onCurrentCommandChange() {}

    /**
     * Create scroll pane for table
     *
     * @param actor the table to add scroll pane
     */
    fun createScrollPane(actor: Actor): ScrollPane = ActorFunction.createScrollPane(skin, actor)

    /**
     * Create split pane
     */
    fun createSplitPane(actor1: Actor, actor2: Actor, vertical: Boolean): SplitPane = ActorFunction.createSplitPane(skin, actor1, actor2, vertical)


    /**
     * Create image
     */
    fun createImage(
        name: String,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image = ActorFunction.createImage(assets, name, r, g, b, a, soundVolume, function)

    /**
     * Create image
     */
    fun createImage(
        id: Int,
        name: String,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image = ActorFunction.createImage(assets, id, name, xPos, yPos, width, height, soundVolume, function)


    /**
     * Create image
     */
    fun createImage(
        name: String,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image = ActorFunction.createImage(assets, name, xPos, yPos, width, height, r, g, b, a, soundVolume, function)

    /**
     * Create image button
     */
    fun createImageButton(
        name: String,
        rUp: Float,
        gUp: Float,
        bUp: Float,
        aUp: Float,
        rDown: Float,
        gDown: Float,
        bDown: Float,
        aDown: Float,
        rChecked: Float,
        gChecked: Float,
        bChecked: Float,
        aChecked: Float,
        soundVolume: Float = 0.5f,
        function: (ImageButton) -> Unit = {},
    ): ImageButton = ActorFunction.createImageButton(
        assets,
        name,
        rUp,
        gUp,
        bUp,
        aUp,
        rDown,
        gDown,
        bDown,
        aDown,
        rChecked,
        gChecked,
        bChecked,
        aChecked,
        soundVolume,
        function
    )


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
        soundVolume: Float = 0.5f,
        function: (TextButton) -> Unit = {},
    ): TextButton = ActorFunction.createTextButton(skin, assets, text, fontSize, soundVolume, function)

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
     * Create slider
     *
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    fun createSlider(
        min: Float,
        max: Float,
        stepSize: Float,
        default: Float,
        vertical: Boolean = false,
        function: (Float, Slider) -> Unit = { _, _ -> },
    ): Slider = ActorFunction.createSlider(skin, min, max, stepSize, default, vertical, function)

    /**
     * Create gdx list
     *
     * @param itemList the list of item to be selected
     * @param fontSize the font size of the select box
     * @param function the function acted after the select box is changed, take this select box as parameter
     */
    inline fun <reified T> createList(
        itemList: List<T>,
        fontSize: Int = 16,
        crossinline function: (T, com.badlogic.gdx.scenes.scene2d.ui.List<T>) -> Unit = { _, _ -> },
    ): com.badlogic.gdx.scenes.scene2d.ui.List<T> = ActorFunction.createList(skin, assets, itemList, fontSize, function)

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

    fun enableActor(actor: Actor) = ActorFunction.enableActor(actor)

    companion object {
        fun <T : Actor> addComponentToClient(game: RelativitizationGame, component: ScreenComponent<T>) {
            game.onGdxSettingsChangeFunctionList.add(component::onGdxSettingsChange)

            val universeClient = game.universeClient
            universeClient.onServerStatusChangeFunctionList.add(component::onServerStatusChange)

            universeClient.onUniverseData3DChangeFunctionList.add(component::onUniverseData3DChange)

            universeClient.onUniverseDataViewChangeFunctionList.add(component::onUniverseDataViewChange)

            universeClient.onPrimarySelectedPlayerIdChangeFunctionList.add(component::onPrimarySelectedPlayerIdChange)
            universeClient.onSelectedPlayerIdListChangeFunctionList.add(component::onSelectedPlayerIdListChange)
            universeClient.onPrimarySelectedInt3DChangeFunctionList.add(component::onPrimarySelectedInt3DChange)

            universeClient.onCurrentCommandChangeFunctionList.add(component::onCurrentCommandChange)
        }

        fun <T : Actor> addAllComponentToClient(game: RelativitizationGame, component: ScreenComponent<T>) {
            component.getScreenComponentList().forEach { addComponentToClient(game, it) }
        }
    }
}