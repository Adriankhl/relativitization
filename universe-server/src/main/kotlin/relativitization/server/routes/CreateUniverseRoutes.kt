package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerInternal
import relativitization.universe.Universe
import relativitization.universe.communication.LoadUniverseMessage
import relativitization.universe.communication.NewUniverseMessage
import relativitization.universe.data.UniverseData
import relativitization.universe.generate.GenerateUniverse

fun Route.createUniverseRouting(universeServerInternal: UniverseServerInternal) {
    // new Universe
    route("/create/new") {
        post {
            val newUniverseMessage: NewUniverseMessage = call.receive()
            if (newUniverseMessage.adminPassword == universeServerInternal.adminPassword) {
                val universeData: UniverseData = GenerateUniverse.generate(newUniverseMessage.generateSetting)
                if (universeData.isUniverseValid()) {
                    universeServerInternal.setUniverse(Universe(universeData))
                    call.respondText("Created Universe", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Create Universe Failed, wrong setting",
                        ContentType.Text.Plain,
                        HttpStatusCode.NotAcceptable
                    )
                } 
            } else {
                call.respondText(
                    "Create Universe Failed, wrong admin password",
                    io.ktor.http.ContentType.Text.Plain,
                    io.ktor.http.HttpStatusCode.Unauthorized
                )
            }
        }
    }

    route("/create/load") {
        post {
            val loadUniverseMessage: LoadUniverseMessage = call.receive()
            if (loadUniverseMessage.adminPassword == universeServerInternal.adminPassword) {
                val universeData: UniverseData = Universe.loadUniverseLatest(loadUniverseMessage.universeName)
                if (universeData.isUniverseValid()) {
                    universeServerInternal.setUniverse(Universe(universeData))
                    call.respondText("Loaded Universe", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Load Universe Failed, wrong setting",
                        ContentType.Text.Plain,
                        HttpStatusCode.NotAcceptable
                    )
                }
            } else {
                call.respondText(
                    "Load Universe Failed, wrong admin password",
                    io.ktor.http.ContentType.Text.Plain,
                    io.ktor.http.HttpStatusCode.Unauthorized
                )
            }
        }
    }
}

fun Application.registerCreateUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        createUniverseRouting(universeServerInternal)
    }
}