package nl.jochem.packetserver.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import nl.jochem.packetserver.packethelpers.Packet

fun getPacketType(input: String) : Packet? {
    return try {
        GsonBuilder().create()!!.fromJson(input, Packet::class.java)
    }catch (error: JsonSyntaxException) {
        null
    } ?:  null

}

fun getPacket(input: String, packet: Class<*>) : Any? {
    return try {
        GsonBuilder().create()!!.fromJson(input, packet)
    }catch (error: JsonSyntaxException) {
        null
    } ?: null
}