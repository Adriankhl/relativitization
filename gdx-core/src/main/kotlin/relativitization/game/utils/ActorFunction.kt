package relativitization.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.MessageFormatData
import relativitization.universe.core.utils.NoTranslateString
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.core.utils.TranslateString
import kotlin.collections.List
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList

object ActorFunction {

    private val logger = RelativitizationLogManager.getLogger("ActorFunction")

    fun translate(text: String, assets: Assets): String {
        val i18NBundle = assets.getI18NBundle()
        return try {
            val trText: String = i18NBundle.format(text)
            if (trText != "") {
                trText
            } else {
                logger.trace("Empty translated text: $text")
                text
            }
        } catch (e: Throwable) {
            logger.trace("No translation for $text")
            text
        }
    }

    fun translate(text: I18NString, assets: Assets): String {
        val i18NBundle = assets.getI18NBundle()
        val messageFormatList: List<MessageFormatData> = text.toMessageFormatList()
        val normalStringList: List<String> = text.toNormalStringList()

        return messageFormatList.mapIndexed { index, varData ->
            try {
                val translatedVariableList: List<String> = varData.variableList.map {
                    when (it) {
                        is NoTranslateString -> it.str
                        is TranslateString -> {
                            val trVar: String = i18NBundle.format(it.str)
                            trVar.ifEmpty {
                                logger.debug("Empty translated variable: $trVar")
                                it.str
                            }
                        }
                    }
                }

                val trText: String = i18NBundle.format(
                    varData.template,
                    *translatedVariableList.toTypedArray()
                )

                trText.ifEmpty {
                    logger.debug("Empty translated text: $text")
                    normalStringList[index]
                }
            } catch (e: Throwable) {
                logger.debug("No translation for $text")
                normalStringList[index]
            }
        }.reduce(String::plus)
    }

    /**
     * Create scroll pane for table
     *
     * @param actor the actor in the scroll pane
     */
    fun createScrollPane(
        skin: Skin,
        actor: Actor,
        onSizeChange: () -> Unit
    ): ScrollPane = GdxScrollPane(actor, skin, onSizeChange)

    /**
     * Create split pane
     */
    fun createSplitPane(
        skin: Skin,
        actor1: Actor,
        actor2: Actor,
        vertical: Boolean
    ): SplitPane {
        val splitPane = SplitPane(actor1, actor2, vertical, skin)

        splitPane.style.handle.minWidth = 10f
        splitPane.style.handle.minHeight = 10f

        return splitPane
    }


    /**
     * Create an Image
     */
    fun createImage(
        assets: Assets,
        name: String,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image {
        val image = assets.getImage(name)
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(image)
            }
        })

        return image
    }

    /**
     * Create an Image
     */
    fun createImage(
        assets: Assets,
        name: String,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image {
        val image = assets.getImage(name, r, g, b, a)
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(image)
            }
        })
        return image
    }

    /**
     * Create an Image
     */
    fun createImage(
        assets: Assets,
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
    ): Image {
        val image = assets.getImage(name, xPos, yPos, width, height, r, g, b, a)
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(image)
            }
        })
        return image
    }

    /**
     * Create an Image
     */
    fun createImage(
        assets: Assets,
        id: Int,
        name: String,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image {
        val image = assets.getImage(id, name, xPos, yPos, width, height)
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(image)
            }
        })
        return image
    }

    /**
     * Create a nine patch image
     */
    fun createNinePatchImage(
        assets: Assets,
        name: String,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        rotation: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        soundVolume: Float = 0.5f,
        function: (Image) -> Unit = {}
    ): Image {
        val image = assets.getNinePatchImage(
            name = name,
            xPos = xPos,
            yPos = yPos,
            width = width,
            height = height,
            rotation = rotation,
            r = r,
            g = g,
            b = b,
            a = a
        )
        image.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(image)
            }
        })
        return image
    }


    /**
     * Create an image button
     *
     * @param name name of the image
     * @param function the function called when clicking this button, take this button as parameter
     */
    fun createImageButton(
        assets: Assets,
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
    ): ImageButton {
        val rawTexture = TextureRegionDrawable(assets.getAtlasRegion(name))

        val button = ImageButton(
            rawTexture.tint(Color(rUp, gUp, bUp, aUp)),
            rawTexture.tint(Color(rDown, gDown, bDown, aDown)),
            rawTexture.tint(Color(rChecked, gChecked, bChecked, aChecked)),
        )

        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val sound = assets.getSound("click1.ogg")
                sound.play(soundVolume)
                function(button)
            }
        })

        return button
    }


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

        return Label(translate(text, assets), style)
    }

    /**
     * Create label to display text in I18NString
     *
     * @param text text to display
     * @param fontSize size of the font
     */
    fun createLabel(skin: Skin, assets: Assets, text: I18NString, fontSize: Int = 16): Label {
        val style = skin.get(Label.LabelStyle::class.java)
        style.font = assets.getFont(fontSize)
        style.fontColor = Color.WHITE

        return Label(translate(text, assets), style)
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

        val button = TextButton(translate(text, assets), style)

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

        style.fontColor = Color(1.0f, 1.0f, 1.0f, 1.0f)

        val checkBox = CheckBox(translate(text, assets), style)

        checkBox.image.setScaling(Scaling.fill)
        checkBox.imageCell.size(fontSize.toFloat())

        checkBox.isChecked = default

        checkBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                function(checkBox.isChecked, checkBox)
            }
        })

        return checkBox
    }


    /**
     * Create slider
     *
     * @param function the function acted after the select box is changed, take this select box as parameter
     */
    fun createSlider(
        skin: Skin,
        min: Float,
        max: Float,
        stepSize: Float,
        default: Float,
        scale: Float,
        vertical: Boolean = false,
        function: (Float, Slider) -> Unit = { _, _ -> },
    ): Slider {
        val slider = Slider(min, max, stepSize, vertical, skin)

        slider.style.knobOver.minWidth = 18f * scale
        slider.style.knobOver.minHeight = 18f * scale
        slider.style.knobDown.minWidth = 18f * scale
        slider.style.knobDown.minHeight = 18f * scale
        slider.style.background.minWidth = 17f * scale
        slider.style.background.minHeight = 11f * scale
        slider.style.knob.minWidth = 18f * scale
        slider.style.knob.minHeight = 18f * scale
        slider.style.knobBefore.minWidth = 17f * scale
        slider.style.knobBefore.minHeight = 11f * scale

        slider.value = default

        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                function(slider.value, slider)
            }
        })
        return slider
    }

    /**
     * Create list
     *
     * @param itemList list of the items
     * @param fontSize the font size of the text
     * @param function the function acted after the text field has changed, take this check box as parameter
     */
    inline fun <reified T> createList(
        skin: Skin,
        assets: Assets,
        itemList: List<T>,
        fontSize: Int = 16,
        crossinline function: (T, GdxList<T>) -> Unit = { _, _ -> }
    ): GdxList<T> {
        val style = skin.get(GdxList.ListStyle::class.java)
        style.font = assets.getFont(fontSize)

        val gdxList: GdxList<T> = object : GdxList<T>(style) {
            override fun toString(item: T): String {
                return if (item is String) {
                    translate(item, assets)
                } else {
                    item.toString()
                }
            }
        }

        gdxList.setItems(Array(itemList.toTypedArray()))

        gdxList.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                function(gdxList.selected, gdxList)
            }
        })

        return gdxList
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

        val selectBox: SelectBox<T> = object : SelectBox<T>(style) {
            override fun toString(item: T): String {
                return if (item is String) {
                    translate(item, assets)
                } else {
                    item.toString()
                }
            }
        }
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

        // This adds a pop-up text box to help text field input in Android
        // Disadvantage: ugly, need to click ok twice
        // Comment this out and empty space at the bottom of scroll pane instead
        /*
        textField.onscreenKeyboard = TextField.OnscreenKeyboard {
            Gdx.input.getTextInput(object : Input.TextInputListener {
                override fun input(text: String?) {
                    textField.text = text
                }

                override fun canceled() {
                    logger.debug("Cancelled text input")
                }
            }, "", textField.text, "")
        }
        */

        return textField
    }


    fun disableActor(actor: Actor) {
        actor.touchable = Touchable.disabled
        actor.color = Color.GRAY
    }


    fun enableActor(actor: Actor) {
        actor.touchable = Touchable.enabled
        actor.color = Color.WHITE
    }
}