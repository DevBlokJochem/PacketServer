plugins {
    kotlin("jvm") version "1.9.0"
    application
    `maven-publish`
}

group = "nl.jochem.packetserver"
version = "2.1"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    //json config things
    implementation("com.google.code.gson:gson:2.8.9")

    // Async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("PacketManagerKt")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.properties["group"] as? String?
            artifactId = project.name
            version = project.properties["version"] as? String?

            from(components["java"])
        }
    }
}