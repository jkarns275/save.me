package me.jkarns.save.me

import com.google.gson.JsonObject

class Ping(val id: Long, val lat: Double, val lon: Double, val timestamp: Long) {

   fun distance(other: Ping): Double {
       val a = (other.lat - this.lat)
       val asq = a * a
       val b = (other.lon - this.lon)
       val bsq = b * b
       return Math.sqrt(asq + bsq)
   }

   fun toJsonObject(): JsonObject {
       val obj = JsonObject()
       obj.addProperty("id", id)
       obj.addProperty("lat", lat)
       obj.addProperty("lon", lon)
       obj.addProperty("timestamp", timestamp)
       return obj
   }
}