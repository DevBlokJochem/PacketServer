package nl.jochem.packetserver.config

import com.google.gson.GsonBuilder
import java.io.File
import java.util.*
import kotlin.collections.HashMap

private const val filename: String = "packetserver/settings.json"

class RegisterSettingsConfig {

    init {
        if (!File(filename).exists()) {
            File(filename).createNewFile()
            File(filename).writeText(GsonBuilder().setPrettyPrinting().create()!!.toJson(PacketServerSettings(
                serverPort = 25800,
                clients = HashMap()
            )))
        }
    }

    fun getInstance() = GsonBuilder().setPrettyPrinting().create()!!.fromJson(File(filename).readText(), PacketServerSettings::class.java)!!

}

data class PacketServerSettings(
    val serverPort: Int,
    val clients: HashMap<UUID, Int>,
)