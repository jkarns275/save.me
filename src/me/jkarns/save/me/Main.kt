package me.jkarns.save.me
import spark.kotlin.*

val ID_MAX_AGE = 60 * 60 * 24
val MIME_HTML = "text/html"
val MIME_JSON = "application/json"

fun main(args: Array<String>) {
    val http: Http = ignite().port(4444).secure("keystore", "password", "truststore", "password")

    http.get("/") {
        val app: App = App()
        this.response.header("Content-Type", MIME_HTML)
        app.index()
    }

    http.post("/ping/:lat/:lon") {
        val app: App = App()
        var id = request.cookie("id")
        if (id == null) {
            id = IdGenerator.next().toString()
            response.cookie("id", id, ID_MAX_AGE)
        }

        val params = request.params()
        println(params)
        val lat = params[":lat"]
        val lon = params[":lon"]

        if (lat != null && lon != null) {
            app.postPing(id, lat, lon)
        }
        "Ok"
    }

    http.post("/pings/:lat/:lon") {
        val app: App = App()
        this.response.header("Content-Type", MIME_JSON)
        app.getPings()
    }

    http.get("/viewpings") {
        App().viewpings()
    }

    http.get("/style.css") {
        this.response.header("Content-Type", "text/css")
        App().style()
    }

    http.get("/bootstrap.css") {
        this.response.header("Content-Type", "text/css")
        App().bootstrap()
    }
}