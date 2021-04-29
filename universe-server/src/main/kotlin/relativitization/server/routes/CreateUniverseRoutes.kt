package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.universe.Universe

fun Route.createUniverseRouting(universe: Universe) {
    route("/create") {
        get {

        }
    }
}

fun Application.registerCreateUniverseRoutes(universe: Universe) {
    routing {
        createUniverseRouting(universe)
    }
}