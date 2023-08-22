package nl.jochem.packetserver.utils

fun Class<*>.createName() : String {
    return this.name.replaceBeforeLast(".", "").replaceFirst(".", "")
}