package nl.jochem.packetserver.packethelpers

import java.util.function.Consumer

class SubscriptionPacket<T : Packet>(val packetType: Class<T>, private val callback: Consumer<T>, private val priority: Int): Comparable<SubscriptionPacket<*>> {

    fun handle(packet: Packet) {
        callback.accept(packetType.cast(packet))
    }

    override fun compareTo(other: SubscriptionPacket<*>): Int = priority - other.priority
}