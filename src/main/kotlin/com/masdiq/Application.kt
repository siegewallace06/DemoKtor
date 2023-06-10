package com.masdiq

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, watchPaths = listOf("classes", "resources")) {
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }
        module()
        module2()
    }.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText { "Halo dunia" }
        }
        get("/test") {
            call.respondText { "test lagi" }
        }
        static {
            resources("static")
        }
        get("/welcome") {
            call.respondHtml {
                val name = call.request.queryParameters["name"]
                head {
                    title { +"Custom Title" }
                }
                body {
                    if (name.isNullOrEmpty()) {
                        h3 { +"Welcome someone!" }
                    } else {
                        h3 { +"Welcome $name" }
                    }
                    p { +"Current dir is: ${System.getProperty("user.dir")}" }
                    img(src = "img.jpeg")
                }
            }
        }
    }
}

fun Application.module2() {
    routing {
        get("/dua") {
            call.respondText { "test dari module 2" }
        }
        get("/test/{name}") {
            val header = call.request.headers["Connection"]
            val name = call.parameters["name"]
            if (name == "Admin") {
                call.response.header(name = "AdminHeader", "Admin")
                call.respond(message = "Hello Admin", status = HttpStatusCode.OK)
            } else {
                call.respondText { "This connection is $header from $name" }
            }
        }
        get("/person") {
            try {
                val person = Person(name = "kosong", age = 0)
                call.respond(message = person, status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(message = "${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
        get("/redirect") {
            call.respondRedirect(url = "/moved", permanent = false)
        }
        get("/moved") {
            call.respondText("Sukses ngeredirect!")
        }
    }
}

@Serializable
data class Person(
    val name: String,
    val age: Int
)