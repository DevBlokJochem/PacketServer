package nl.jochem.packetserver.packethelpers

import nl.jochem.packetserver.PacketManager
import java.util.UUID

open class Packet(
    open val packetID: String,
    val sender: UUID = PacketManager.getConfig().serverID
)