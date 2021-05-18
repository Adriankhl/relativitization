package relativitization.game.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.apache.logging.log4j.LogManager


class Assets {
    private val manager: AssetManager = AssetManager()

    private val resolver: FileHandleResolver = InternalFileHandleResolver()

    private val textureMap: MutableMap<String, AtlasRegion> = mutableMapOf()

    fun loadAll() {
        // https://stackoverflow.com/questions/46619234/libgdx-asset-manager-load-true-type-font
        manager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        manager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))

        val skinParameter = SkinParameter("skin/flat-earth-ui.atlas")
        manager.load("skin/flat-earth-ui.json", Skin::class.java, skinParameter)

        manager.load("music/Alexander Ehlers - Warped.mp3", Music::class.java)

        manager.load("relativitization-asset.atlas", TextureAtlas::class.java)

        manager.load("sounds/click1.ogg", Sound::class.java)

        for (fontSize in 8..40) {
            val smallFont = FreeTypeFontLoaderParameter()
            smallFont.fontFileName = "fonts/nerd.ttf"
            smallFont.fontParameters.size = fontSize
            manager.load("nerd$fontSize.ttf", BitmapFont::class.java, smallFont)
        }

        manager.finishLoading()
    }

    fun dispose() {
        manager.dispose()
    }

    fun getSkin(): Skin = manager.get("skin/flat-earth-ui.json")

    fun getBackgroundMusic(): Music = manager.get("music/Alexander Ehlers - Warped.mp3")

    private fun getAtlasRegion(name: String): AtlasRegion {
        val textureAtLas: TextureAtlas = manager.get("relativitization-asset.atlas")
        return textureMap.getOrPut(name) { textureAtLas.findRegion(name) }
    }

    fun getImage(name: String): Image {
        val region = getAtlasRegion(name)
        return Image(region)
    }

    fun getImage(name: String, r: Float, g: Float, b: Float, a: Float): Image {
        val region = getAtlasRegion(name)
        val textureRegionDrawable = TextureRegionDrawable(region)
        return Image(textureRegionDrawable.tint(Color(r, g, b, a)))
    }

    fun getBackGroundColor(r: Float, g: Float, b: Float, a: Float): Drawable {
        val region = getAtlasRegion("background/white-pixel")
        val textureRegionDrawable = TextureRegionDrawable(region)
        return textureRegionDrawable.tint(Color(r, g, b, a))
    }

    fun getFont(fontSize: Int): BitmapFont {
        val actualSize = when {
            fontSize < 8 -> {
                logger.debug("Font cannot be smaller than 8")
                8
            }
            fontSize > 40 -> {
                logger.debug("Font cannot be larger than 40")
                40
            }
            else -> {
                fontSize
            }
        }

        return manager.get("nerd$actualSize.ttf")
    }

    fun getSound(name: String): Sound = manager.get("sounds/$name")

    companion object {
        private val logger = LogManager.getLogger()
    }
}