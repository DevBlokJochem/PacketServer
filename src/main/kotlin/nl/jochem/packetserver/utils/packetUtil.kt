package nl.jochem.packetserver.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packets.ServerOpenPacket
import java.util.function.Consumer


fun getPacketType(input: String) : Packet? {
    return try {
        GsonBuilder().create()!!.fromJson(input, Packet::class.java)
    }catch (error: JsonSyntaxException) {
        null
    }

}

fun getPacket(input: String, packet: Class<*>) : Any? {
    return try {
        GsonBuilder().create()!!.fromJson(input, packet)
    }catch (error: JsonSyntaxException) {
        null
    }
}

fun getServerOpenPacket(input: String) : ServerOpenPacket? {
    return try {
        GsonBuilder().create()!!.fromJson(input, ServerOpenPacket::class.java)
    }catch (error: JsonSyntaxException) {
        null
    }
}

fun <T : Packet> getSpecificPacket(input: String, type: Class<T>) : Packet? {
    return try {
        GsonBuilder().create()!!.fromJson(input, type)
    }catch (error: JsonSyntaxException) {
        null
    }
}