package relativitization.game.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin


class Assets {
    private val manager: AssetManager = AssetManager()

    private val resolver: FileHandleResolver = InternalFileHandleResolver()

    private val textureMap: MutableMap<String, AtlasRegion> = mutableMapOf()

    fun loadAll() {
        manager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        manager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))

        val flatUISkin = SkinLoader.SkinParameter("skin/flatearth/flatearthui/flat-earth-ui.atlas")
        manager.load("skin/flatearth/flatearthui/flat-earth-ui.json", Skin::class.java, flatUISkin)

        manager.load("music/Alexander Ehlers - Warped.mp3", Music::class.java)

        manager.load("relativitization-asset.atlas", TextureAtlas::class.java)

        val smallFont = FreeTypeFontLoaderParameter()
        smallFont.fontFileName = "fonts/nerd.ttf"
        smallFont.fontParameters.size = 10
        manager.load("nerd10.ttf", BitmapFont::class.java, smallFont)

        manager.finishLoading()
    }

    fun dispose() {
        manager.dispose()
    }

    fun getSkin(): Skin = manager.get("skin/flatearth/flatearthui/flat-earth-ui.json")

    fun getBackgroundMusic(): Music = manager.get("music/Alexander Ehlers - Warped.mp3")

    fun getImage(name: String): Image {
        val textureAtLas: TextureAtlas = manager.get("relativitization-asset.atlas")
        val region = textureMap.getOrPut(name) { textureAtLas.findRegion(name) }
        return Image(region)
    }

    fun getSmallFont(): BitmapFont = manager.get("nerd10.ttf", BitmapFont::class.java)
}