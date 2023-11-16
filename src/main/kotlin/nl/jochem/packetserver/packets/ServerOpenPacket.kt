package nl.jochem.packetserver.packets

import nl.jochem.packetserver.packethelpers.Packet
import java.util.UUID

data class ServerOpenPacket(
    val serverID: UUID
) : Packet(ID) {
    companion object {
        val ID = "ServerOpenPacket"
    }
}