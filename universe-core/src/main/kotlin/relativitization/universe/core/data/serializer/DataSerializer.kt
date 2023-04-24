package relativitization.universe.core.data.serializer

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object DataSerializer {
    private var format: Json = Json {
        encodeDefaults = true
    }

    fun getJsonFormat(): Json = format

    fun updateJsonFormatModule(module: SerializersModule) {
        format = Json {
            encodeDefaults = true
            serializersModule = module
        }
    }

    inline fun <reified I> encode(data: I): String {
        return getJsonFormat().encodeToString(data)
    }

    inline fun <reified O> decode(str: String): O {
        return getJsonFormat().decodeFromString(str)
    }

    /**
     * Deep copy of data classes by serialization
     */
    inline fun <reified I, reified O> copy(data: I): O {
        return decode(encode(data))
    }
}