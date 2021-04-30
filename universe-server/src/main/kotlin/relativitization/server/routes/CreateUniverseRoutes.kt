package relativitization.server.routes

import io.ktor.application.*
import io.ktor.routing.*
import relativitization.server.UniverseServerState
import relativitization.universe.Universe

fun Route.createUniverseRouting(universe: Universe, serverState: UniverseServerState) {
    route("/create") {
        get {

        }
    }
}

fun Application.registerCreateUniverseRoutes(universe: Universe, serverState: UniverseServerState) {
    routing {
        createUniverseRouting(universe, serverState)
    }
}