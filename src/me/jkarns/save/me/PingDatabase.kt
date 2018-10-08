package me.jkarns.save.me

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class PingDatabase {

    object PingDatabase {
        val USERNAME = "josh"
        val PASSWORD = ""
        val PORT = 3306
        val URI = "jdbc:mysql://localhost:$PORT/mysql?user=$USERNAME&password=$PASSWORD&serverTimezone=UTC&databaseName=pings;"
        val DB_NAME = "pings"
        val TABLE_NAME = "pings2"
        val FIELD_ID        = "id"

        val ALL_PINGS = "SELECT * FROM $DB_NAME.$TABLE_NAME"
        val FIELD_LATITUDE  = "lat"
        val FIELD_LONGITUDE = "lon"
        val FIELD_TIMESTAMP = "timestamp"
    }

    var connection: Connection? = null

    init {
        val connectionProperties = Properties()
        connectionProperties.put("user", PingDatabase.USERNAME)
        connectionProperties.put("pass", PingDatabase.PASSWORD)

        try {
            connection = DriverManager.getConnection(PingDatabase.URI)
        } catch (se: SQLException) {
            println("Failed to initialize SQL Server, encountered the following error:")
            println(se)
            System.exit(-1)
        }
    }

    fun writePing(ping: Ping) {
        val statement = connection!!.createStatement()
        try {
            statement.executeUpdate("DELETE FROM ${PingDatabase.DB_NAME}.${PingDatabase.TABLE_NAME} WHERE id = ${ping.id}")
            statement.executeUpdate("INSERT INTO ${PingDatabase.DB_NAME}.${PingDatabase.TABLE_NAME} VALUES " +
                    "(${ping.id}, ${ping.lat}, ${ping.lon}, ${ping.timestamp})")
        } catch (se: SQLException) {
            println("Encountered SQL error:")
            println(se)
            se.printStackTrace()
        } finally {
            statement.close()
        }
    }

    fun fetchAllPings(): List<Ping>? {
        val statement = connection!!.createStatement()
        try {
            val rs = statement.executeQuery(PingDatabase.ALL_PINGS)

            val pings = ArrayList<Ping>()

            while (rs.next()) {
                pings.add(Ping(
                        rs.getLong(PingDatabase.FIELD_ID),
                        rs.getDouble(PingDatabase.FIELD_LATITUDE),
                        rs.getDouble(PingDatabase.FIELD_LONGITUDE),
                        rs.getLong(PingDatabase.FIELD_TIMESTAMP)))
            }

            return pings

        } catch (se: SQLException) {
            println("Encountered SQL error:")
            println(se)
            se.printStackTrace()
            return null
        } finally {
            statement.close()
        }
    }

}