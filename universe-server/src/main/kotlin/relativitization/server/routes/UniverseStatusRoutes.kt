package relativitization.server.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerState
import relativitization.universe.Universe

fun Route.universeStatueRouting(universe: Universe, serverState: UniverseServerState) {
    route("/status/hello") {
        get {
            call.respondText("Hello, world!")
        }
    }
}

fun Application.registerUniverseStatusRoutes(universe: Universe, serverState: UniverseServerState) {
    routing {
        universeStatueRouting(universe, serverState)
    }
}
