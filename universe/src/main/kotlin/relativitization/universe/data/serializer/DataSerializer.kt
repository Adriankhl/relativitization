package relativitization.universe.data.serializer

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.reflect.KClass

object DataSerializer {
    val format = Json {
        encodeDefaults = true
    }

    /**
     * Deep copy of data classes by serialization
     */
    inline fun <reified I, reified O> copy(data: I): O {
        val str: String = format.encodeToString(data)
        return format.decodeFromString(str)
    }
}