package relativitization.server.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerState
import relativitization.universe.Universe

fun Route.universeStatueRouting(serverState: UniverseServerState) {
    route("/status/hello") {
        get {
            call.respondText("Hello, world!")
        }
    }
}

fun Application.registerUniverseStatusRoutes(serverState: UniverseServerState) {
    routing {
        universeStatueRouting(serverState)
    }
}
