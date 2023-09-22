package nl.jochem.packetserver.packets

import nl.jochem.packetserver.packethelpers.Packet
import java.util.*

data class ServerClosePacket(
    val serverID: UUID,
    override val target: UUID? = null
) : Packet("ServerClosePacket", target)