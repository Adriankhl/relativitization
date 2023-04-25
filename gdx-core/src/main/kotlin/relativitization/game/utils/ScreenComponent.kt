package relativitization.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.universe.core.maths.number.Notation
import relativitization.universe.core.maths.number.ScientificNotation
import relativitization.universe.core.maths.number.toScientificNotation
import relativitization.universe.core.maths.physics.Double2D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.utils.I18NString
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

abstract class ScreenComponent<out T : Actor>(val assets: Assets) {
    val skin: Skin = assets.getSkin()

    private val allChildScreenComponentList: MutableList<ScreenComponent<Actor>> = mutableListOf()

    val commandButtonColor = Color(0.5f, 1.0f, 0.5f, 1.0f)

    /**
     * Get the actor (e.g. table, group) of this component
     */
    abstract fun getScreenComponent(): T

    /**
     * Add all components of a child component to this component
     *
     * @param component a child component
     */
    fun addChildScreenComponent(component: ScreenComponent<Actor>) {
        allChildScreenComponentList.addAll(component.getScreenComponentList())
    }

    /**
     * Remove all components of a child component to this component
     *
     * @param component a child component
     */
    fun removeChildScreenComponent(component: ScreenComponent<Actor>) {
        allChildScreenComponentList.removeAll(component.getScreenComponentList().toSet())
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
     * To ensure thread safety, don't modify any libgdx-related function here.
     * Instead, call Gdx.graphics.requestRendering() and modify libgdx things in the render method
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
     * Call this function when selecting different primary int3D
     */
    open fun onPrimarySelectedInt3DChange() {}

    /**
     * Call this function when changing first selected id
     */
    open fun onPrimarySelectedPlayerIdChange() {}

    /**
     * Call this function when changing selectedPlayerId
     */
    open fun onSelectedPlayerIdListChange() {}

    /**
     * Call this function when changing mapCenterPlayerId
     */
    open fun onMapCenterPlayerIdChange() {}

    /**
     * Call this function when changing commandList
     */
    open fun onCommandListChange() {}

    /**
     * Call this function when command to be confirmed change
     */
    open fun onCurrentCommandChange() {}

    /**
     * Call this when isPlayerDead is changed, may get called at each iteration of universe client
     * To ensure thread safety, don't modify any libgdx-related function here.
     * Instead, call Gdx.graphics.requestRendering() and modify libgdx things in the render method
     */
    open fun onIsPlayerDeadChange() {}

    /**
     * Call this when the selected point at the knowledge plane is changed
     */
    open fun onSelectedKnowledgeDouble2DChange() {}

    /**
     * Translate text
     */
    fun translate(text: String): String = ActorFunction.translate(text, assets)


    /**
     * Translate text in I18NString
     */
    fun translate(text: I18NString): String = ActorFunction.translate(text, assets)

    /**
     * Create scroll pane for table
     *
     * @param actor the table to add scroll pane
     */
    fun createScrollPane(
        actor: Actor,
        onSizeChanged: () -> Unit = {}
    ): ScrollPane = ActorFunction.createScrollPane(skin, actor, onSizeChanged)

    /**
     * Create split pane
     */
    fun createSplitPane(actor1: Actor, actor2: Actor, vertical: Boolean): SplitPane =
        ActorFunction.createSplitPane(skin, actor1, actor2, vertical)


    /**
     * Create image
     */
    fun createImage(
        name: String,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        soundVolume: Float,
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
        soundVolume: Float,
        function: (Image) -> Unit = {}
    ): Image = ActorFunction.createImage(
        assets,
        id,
        name,
        xPos,
        yPos,
        width,
        height,
        soundVolume,
        function
    )

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
        soundVolume: Float,
        function: (Image) -> Unit = {}
    ): Image = ActorFunction.createImage(
        assets,
        name,
        xPos,
        yPos,
        width,
        height,
        r,
        g,
        b,
        a,
        soundVolume,
        function
    )

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
        soundVolume: Float,
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
    fun createLabel(text: String, fontSize: Int): Label =
        ActorFunction.createLabel(skin, assets, text, fontSize)


    /**
     * Create label to display text in I18NString
     *
     * @param text text to display
     * @param fontSize size of the font
     */
    fun createLabel(text: I18NString, fontSize: Int): Label =
        ActorFunction.createLabel(skin, assets, text, fontSize)


    /**
     * Create a text button
     *
     * @param text the text in the button
     * @param fontSize size of the font
     * @param function the function called when clicking this button, take this button as parameter
     * @param extraColor adding color to the background
     */
    fun createTextButton(
        text: String,
        fontSize: Int,
        soundVolume: Float,
        extraColor: Color = Color.WHITE,
        function: (TextButton) -> Unit = {},
    ): TextButton {
        val button: TextButton = ActorFunction.createTextButton(
            skin,
            assets,
            text,
            fontSize,
            soundVolume,
            function
        )

        button.color = extraColor

        return button
    }

    /**
     * Create check box, should use createTickImageButton instead
     *
     * @param text Description of the option
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    private fun createCheckBox(
        text: String,
        default: Boolean,
        fontSize: Int,
        function: (Boolean, CheckBox) -> Unit = { _, _ -> }
    ): CheckBox = ActorFunction.createCheckBox(skin, assets, text, default, fontSize, function)

    fun createTickImageButton(
        default: Boolean,
        soundVolume: Float,
        function: (Boolean) -> Unit = { _ -> }
    ): ImageButton {
        val tickImage = createImageButton(
            name = "basic/white-tick",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 0.5f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = soundVolume
        ) {
            function(it.isChecked)
        }
        tickImage.isChecked = default

        return tickImage
    }

    /**
     * Create slider, prefer createSliderContainer
     *
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    private fun createSlider(
        min: Float,
        max: Float,
        stepSize: Float,
        default: Float,
        scale: Float,
        vertical: Boolean = false,
        function: (Float, Slider) -> Unit = { _, _ -> },
    ): Slider = ActorFunction.createSlider(
        skin = skin,
        min = min,
        max = max,
        stepSize = stepSize,
        default = default,
        scale = scale,
        vertical = vertical,
        function = function
    )

    /**
     * Create a slider with a specific width and height
     */
    fun createSliderContainer(
        min: Float,
        max: Float,
        stepSize: Float,
        default: Float,
        width: Float,
        height: Float,
        vertical: Boolean = false,
        function: (Float, Slider) -> Unit = { _, _ -> },
    ): Container<Slider> {
        val slider = createSlider(
            min = min,
            max = max,
            stepSize = stepSize,
            default = default,
            scale = height / 15f,
            vertical = vertical,
            function = function
        )
        val container = Container(slider)
        container.isTransform = true
        container.size(width, height)
        return container
    }

    /**
     * Create gdx list
     *
     * @param itemList the list of item to be selected
     * @param fontSize the font size of the select box
     * @param function the function acted after the select box is changed, take this select box as parameter
     */
    inline fun <reified T> createList(
        itemList: List<T>,
        fontSize: Int,
        crossinline function: (T, com.badlogic.gdx.scenes.scene2d.ui.List<T>) -> Unit = { _, _ -> },
    ): com.badlogic.gdx.scenes.scene2d.ui.List<T> =
        ActorFunction.createList(skin, assets, itemList, fontSize, function)

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
        fontSize: Int,
        crossinline function: (T, SelectBox<T>) -> Unit = { _, _ -> },
    ): SelectBox<T> =
        ActorFunction.createSelectBox(skin, assets, itemList, default, fontSize, function)


    /**
     * Create text field
     *
     * @param default the default value of the text
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this text field as parameter
     */
    fun createTextField(
        default: String,
        fontSize: Int,
        function: (String, TextField) -> Unit = { _, _ -> }
    ): TextField = ActorFunction.createTextField(skin, assets, default, fontSize, function)

    /**
     * Create an arrow
     *
     * @param arrowWidth the width of the arrow, which is the height of the image
     */
    fun createArrow(
        from: Double2D,
        to: Double2D,
        arrowWidth: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
    ): Image {
        // Create new nine patch and edit this
        val arrowNinePatch =
            NinePatch(assets.getNinePatch("basic/white-right-arrow-tight"))

        // arrow length depends on the end point
        val arrowLength: Float = Intervals.distance(from, to).toFloat()

        // Scale the arrow
        val xScale: Float = arrowLength / arrowNinePatch.totalWidth
        val yScale: Float = arrowWidth / arrowNinePatch.totalHeight
        arrowNinePatch.scale(xScale, yScale)

        // Create the image from the arrow nine patch
        val image = Image(arrowNinePatch)

        // Rotate and set position by the center of the image
        val rotation: Double = atan2(to.y - from.y, to.x - from.x) * 0.5 / PI * 360
        val xCenter: Float = ((from.x + to.x) * 0.5).toFloat()
        val yCenter: Float = ((from.y + to.y) * 0.5).toFloat()
        image.setOrigin(Align.center)
        image.setPosition(xCenter, yCenter, Align.center)
        image.rotation = rotation.toFloat()

        image.setColor(r, g, b, a)

        // Arrow is not clickable
        //image.addListener(object : ClickListener() {
        //    override fun clicked(event: InputEvent?, x: Float, y: Float) {
        //        val sound = assets.getSound("click1.ogg")
        //        sound.play(soundVolume)
        //        function(image)
        //    }
        //})

        image.touchable = Touchable.disabled

        return image
    }

    /**
     * Create a slider, a minus button and a plus button to change arbitrary Double number
     *
     * @param default the default value
     * @param sliderStepSize the step size of the slider
     * @param sliderDecimalPlace the decimal place of the slider
     * @param buttonSize the size of the button
     * @param buttonSoundVolume the sound volume of the button
     * @param currentValue the current value of the Double variable to be changed
     * @param function the function to change the Double variable
     */
    fun createDoubleSliderButtonTable(
        default: Double,
        sliderStepSize: Float,
        sliderDecimalPlace: Int,
        buttonSize: Float,
        sliderScale: Float,
        buttonSoundVolume: Float,
        currentValue: () -> Double,
        function: (Double) -> Unit = { },
    ): Table {
        val nestedTable = Table()

        val coefficientSlider = createSlider(
            min = 1f,
            max = 10f - 10f.pow(-sliderDecimalPlace),
            stepSize = sliderStepSize,
            default = default.toScientificNotation().coefficient.toFloat(),
            scale = sliderScale,
        ) { fl, _ ->
            val originalValue: ScientificNotation = currentValue().toScientificNotation()
            val newCoefficient: Double = Notation.roundDecimal(fl.toDouble(), sliderDecimalPlace)
            val newExponent: Int = originalValue.exponent

            val newValue = ScientificNotation(
                newCoefficient,
                newExponent
            )

            function(newValue.toDouble())
        }

        val exponentMinusButton: ImageButton = createImageButton(
            name = "basic/white-minus",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = buttonSoundVolume
        ) {
            val originalValue: ScientificNotation = currentValue().toScientificNotation()
            val newExponent: Int = originalValue.exponent - 1
            val actualNewExponent: Int = if (newExponent < -300) {
                300
            } else {
                newExponent
            }
            val newCoefficient: Double =
                Notation.roundDecimal(originalValue.coefficient, sliderDecimalPlace)

            val newValue = ScientificNotation(
                newCoefficient,
                actualNewExponent,
            )

            function(newValue.toDouble())
        }

        val exponentPlusButton: ImageButton = createImageButton(
            name = "basic/white-plus",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = buttonSoundVolume
        ) {
            val originalValue: ScientificNotation = currentValue().toScientificNotation()
            val newExponent: Int = originalValue.exponent + 1
            val actualNewExponent: Int = if (newExponent > 300) {
                300
            } else {
                newExponent
            }
            val newCoefficient: Double =
                Notation.roundDecimal(originalValue.coefficient, sliderDecimalPlace)

            val newValue = ScientificNotation(
                newCoefficient,
                actualNewExponent,
            )

            function(newValue.toDouble())
        }

        nestedTable.add(coefficientSlider)

        nestedTable.add(exponentMinusButton).size(
            buttonSize,
            buttonSize
        )

        nestedTable.add(exponentPlusButton).size(
            buttonSize,
            buttonSize
        )

        return nestedTable
    }

    fun createDoubleTextField(
        default: Double,
        fontSize: Int,
    ): DoubleTextField = DoubleTextField(skin, assets, default, fontSize)


    fun createIntTextField(
        default: Int,
        fontSize: Int,
    ): IntTextField = IntTextField(skin, assets, default, fontSize)

    fun disableActor(actor: Actor) = ActorFunction.disableActor(actor)

    fun enableActor(actor: Actor) = ActorFunction.enableActor(actor)

    companion object {
        private fun <T : Actor> addComponentToClient(
            game: RelativitizationGame,
            component: ScreenComponent<T>
        ) {
            game.onGdxSettingsChangeFunctionList.add(component::onGdxSettingsChange)

            val universeClient = game.universeClient
            runBlocking {
                universeClient.addToOnServerStatusChangeFunctionList(component::onServerStatusChange)
            }

            universeClient.onUniverseData3DChangeFunctionList.add(component::onUniverseData3DChange)

            universeClient.onUniverseDataViewChangeFunctionList.add(component::onUniverseDataViewChange)

            universeClient.onPrimarySelectedInt3DChangeFunctionList.add(component::onPrimarySelectedInt3DChange)
            universeClient.onPrimarySelectedPlayerIdChangeFunctionList.add(component::onPrimarySelectedPlayerIdChange)
            universeClient.onSelectedPlayerIdListChangeFunctionList.add(component::onSelectedPlayerIdListChange)

            universeClient.onMapCenterPlayerIdChangeFunctionList.add(component::onMapCenterPlayerIdChange)

            universeClient.onCommandListChangeFunctionList.add(component::onCommandListChange)
            universeClient.onCurrentCommandChangeFunctionList.add(component::onCurrentCommandChange)

            universeClient.onIsPlayerDeadChangeFunctionList.add(component::onIsPlayerDeadChange)

            universeClient.onSelectedKnowledgeDouble2D.add(component::onSelectedKnowledgeDouble2DChange)
        }

        private fun <T : Actor> removeComponentFromClient(
            game: RelativitizationGame,
            component: ScreenComponent<T>
        ) {
            game.onGdxSettingsChangeFunctionList.removeIf {
                it == component::onGdxSettingsChange
            }

            val universeClient = game.universeClient
            runBlocking {
                universeClient.removeFromOnServerStatusChangeFunctionList(component::onServerStatusChange)
            }

            universeClient.onUniverseData3DChangeFunctionList.removeIf {
                it == component::onUniverseData3DChange
            }

            universeClient.onUniverseDataViewChangeFunctionList.removeIf {
                it == component::onUniverseDataViewChange
            }

            universeClient.onPrimarySelectedInt3DChangeFunctionList.removeIf {
                it == component::onPrimarySelectedInt3DChange
            }
            universeClient.onPrimarySelectedPlayerIdChangeFunctionList.removeIf {
                it == component::onPrimarySelectedPlayerIdChange
            }
            universeClient.onSelectedPlayerIdListChangeFunctionList.removeIf {
                it == component::onSelectedPlayerIdListChange
            }

            universeClient.onMapCenterPlayerIdChangeFunctionList.removeIf {
                it == component::onMapCenterPlayerIdChange
            }

            universeClient.onCommandListChangeFunctionList.removeIf {
                it == component::onCommandListChange
            }
            universeClient.onCurrentCommandChangeFunctionList.removeIf {
                it == component::onCurrentCommandChange
            }

            universeClient.onIsPlayerDeadChangeFunctionList.removeIf {
                it == component::onIsPlayerDeadChange
            }

            universeClient.onSelectedKnowledgeDouble2D.removeIf {
                it == component::onSelectedKnowledgeDouble2DChange
            }
        }

        fun <T : Actor> addAllComponentToClient(
            game: RelativitizationGame,
            component: ScreenComponent<T>
        ) {
            component.getScreenComponentList().forEach { addComponentToClient(game, it) }
        }

        fun <T : Actor> removeAllComponentFromClient(
            game: RelativitizationGame,
            component: ScreenComponent<T>
        ) {
            component.getScreenComponentList().forEach { removeComponentFromClient(game, it) }
        }
    }
}