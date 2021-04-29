package relativitization.server

import relativitization.universe.data.serializer.DataSerializer
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.serialization.*
import org.apache.logging.log4j.LogManager
import relativitization.universe.Universe


class UniverseServer {
    lateinit var universe: Universe

    suspend fun start() {
        embeddedServer(
            CIO,
            configure = {
                connectionIdleTimeoutSeconds = 45
            },
            environment = applicationEngineEnvironment {
                
                module {

                }

                connector {
                    port = 29979
                    host = "127.0.0.1"
                }

            }
        ).start(true)
    }
}