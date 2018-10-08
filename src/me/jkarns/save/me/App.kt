package me.jkarns.save.me

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import com.google.gson.*
import me.jkarns.save.me.App.App.API_KEY
import me.jkarns.save.me.App.App.STD_SCOPE
import spark.template.mustache.MustacheTemplateEngine
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.ParseException
import java.util.*

class App {

    object App {
        fun timestamp(): Long { return System.currentTimeMillis() / 1000L }

        fun includeFile(path: String): String {
            try {
                return Scanner(File(path)).useDelimiter("\\Z").next()
            } catch (ioe: IOException) {
                println("Failed to statically import file located at \"$path\"")
                System.exit(-1)
            }
            return ""
        }

        val API_KEY = "AIzaSyDrMOsbNq2lwbeb1fXtC0fC0Vh9Cbo5epo"
        val STD_HEAD = includeFile("views/head.mustache")
        val STD_NAVBAR = includeFile("views/navbar.mustache")
        val STD_SCOPE = HashMap<String, String>()
        val STYLE = includeFile("css/flat-ui.min.css")

        init {
            STD_SCOPE.put("std_head", STD_HEAD)
            STD_SCOPE.put("api_key", API_KEY)
            STD_SCOPE.put("navbar", STD_NAVBAR)
        }
    }

    val database: PingDatabase = PingDatabase()
    val mustacheFactory: MustacheFactory
//    val indexMustache: Mustache
//    val viewpingsMustache: Mustache

    init {
        mustacheFactory = DefaultMustacheFactory()
        //indexMustache = mustacheFactory.compile("views/index.mustache")
        //viewpingsMustache = mustacheFactory.compile("views/viewpings.mustache")
    }

    fun dynamic(path: String): String {
        val stringWriter = StringWriter()
        mustacheFactory.compile(path).execute(stringWriter, STD_SCOPE)
        return stringWriter.toString()
    }

    // Adds the ping to the database...
    fun postPing(id: String, lat: String, lng: String) {
        val idl: Long
        val latd: Double
        val lngd: Double
        try {
            idl = id.toLong()
            latd = lat.toDouble()
            lngd = lng.toDouble()
        } catch (nfe: NumberFormatException) {
            println("Received il-formed latitude, longitude, or idl string(s) (lat: $lat, lng: $lng, id: $id)")
            return
        }
        database.writePing(Ping(idl, latd, lngd, App.timestamp()))
    }

    // JSON serialized list of all pings.
    fun getPings(): String {
        val pings = database.fetchAllPings()
        if (pings == null) {
            println("Failed retrieve all pings from database")
            System.exit(-1)
        }

        val map = HashMap<Long, LinkedList<Ping>>()
        for (ping in pings!!) {
            val list = map.computeIfAbsent(ping.id) { LinkedList() }
            list.addFirst(ping)
        }
        for (list in map.values) {
            list.sortBy { it.timestamp }
        }

        val json = JsonObject()
        for ((id, pings) in map) {
            val arr = JsonArray()
            for (ping in pings)
                arr.add(ping.toJsonObject())
            json.add(id.toString(), arr)
        }

        return json.toString()
    }

    fun index(): String {
        /*val stringWriter = StringWriter()
        indexMustache.execute(stringWriter, STD_SCOPE)
        return stringWriter.toString()*/
        return dynamic("views/index.mustache")
    }

    fun viewpings(): String {
        /*
        val stringWriter = StringWriter()
        viewpingsMustache.execute(stringWriter, STD_SCOPE)
        return stringWriter.toString()
        */
        return dynamic("views/viewpings.mustache")
    }

    fun style(): String {
        // return App.STYLE
        return dynamic("css/flat-ui.min.css")
    }

    fun bootstrap(): String {
        return dynamic("css/bootstrap.min.css")
    }
}
