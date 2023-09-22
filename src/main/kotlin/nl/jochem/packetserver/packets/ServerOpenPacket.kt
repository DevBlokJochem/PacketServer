package nl.jochem.packetserver.packets

import nl.jochem.packetserver.packethelpers.Packet
import java.util.UUID

data class ServerOpenPacket(
    val serverID: UUID,
    override val target: UUID? = null
) : Packet("ServerOpenPacket", target)