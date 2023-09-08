package nl.jochem.packetserver.manager

import com.google.gson.GsonBuilder
import nl.jochem.packetserver.config.RegisterSettingsConfig
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacket
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.function.Consumer

abstract class PacketControl {

    private val listeners: ArrayList<SubscriptionPacket<*>> = ArrayList()
    private val logged = RegisterSettingsConfig().getInstance().logs

    fun send(packet: Packet, writer: OutputStream) {
        if(logged) println("Send: ${packet.packetID} (${packet::class.java.createName()})")
        writer.write((GsonBuilder().create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
    }

    fun <T : Packet> subscribe(type: Class<T>, callback: Consumer<T>): SubscriptionPacket<T> {
        val subscription: SubscriptionPacket<T> = SubscriptionPacket(type, callback)
        listeners.add(subscription)
        return subscription
    }

    fun recieve(input: String, packet: Packet) {
        if(logged) println("Receive: ${packet.packetID}")
        listeners.filter { sub -> packet.packetID == sub.packetType.createName() }.forEach {sub ->
            val packet = getPacket(input, sub.packetType)
            if(packet != null) {
                sub.handle(packet as Packet)
            }
        }
    }

    abstract fun disable()

}