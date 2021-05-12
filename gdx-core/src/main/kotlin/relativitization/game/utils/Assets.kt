package relativitization.game.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Assets {
    private val manager: AssetManager = AssetManager()

    private val textureMap: MutableMap<String, AtlasRegion> = mutableMapOf()

    fun loadAll() {
        val flatUISkin = SkinLoader.SkinParameter("skin/flatearth/flatearthui/flat-earth-ui.atlas")
        manager.load("skin/flatearth/flatearthui/flat-earth-ui.json", Skin::class.java, flatUISkin)

        manager.load("music/Alexander Ehlers - Warped.mp3", Music::class.java)

        manager.load("relativitization-asset.atlas", TextureAtlas::class.java)

        manager.finishLoading()
    }

    fun getSkin(): Skin = manager.get("skin/flatearth/flatearthui/flat-earth-ui.json")

    fun getBackgroundMusic(): Music = manager.get("music/Alexander Ehlers - Warped.mp3")

    fun getImage(name: String): Image {
        val textureAtLas: TextureAtlas = manager.get("relativitization-asset.atlas")
        val region = textureMap.getOrPut(name) { textureAtLas.findRegion(name) }
        return Image(region)
    }
}