package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.properties.Delegates

class DoubleTextField(
    skin: Skin,
    assets: Assets,
    default: Double,
    fontSize: Int,
) {
    private val onNumChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var value: Double by Delegates.observable(default) { _, _, _ ->
        onNumChangeFunctionList.forEach { it() }
    }
    val textField: TextField = ActorFunction.createTextField(
        skin,
        assets,
        default.toString(),
        fontSize
    ){ s, _ ->
        val newNum: Double = try {
            s.toDouble()
        } catch (e: NumberFormatException) {
            logger.debug("Invalid Double")
            value
        }

        if (newNum != value) {
            logger.debug("New Double num: $newNum")
            value = newNum
        }
    }
    init {
        onNumChangeFunctionList.add {
            textField.text = value.toString()
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}

class IntTextField(
    skin: Skin,
    assets: Assets,
    default: Int,
    fontSize: Int,
) {
    private val onNumChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var value: Int by Delegates.observable(default) { _, _, _ ->
        onNumChangeFunctionList.forEach { it() }
    }
    val textField: TextField = ActorFunction.createTextField(
        skin,
        assets,
        default.toString(),
        fontSize
    ){ s, _ ->
        val newNum: Int = try {
            s.toInt()
        } catch (e: NumberFormatException) {
            logger.debug("Invalid Int")
            value
        }

        if (newNum != value) {
            logger.debug("New Int num: $newNum")
            value = newNum
        }
    }
    init {
        onNumChangeFunctionList.add {
            textField.text = value.toString()
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}