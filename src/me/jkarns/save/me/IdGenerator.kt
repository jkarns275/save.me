package me.jkarns.save.me

object IdGenerator {
    var id: Long = 0L

    fun next(): Long { return id++ }
}
