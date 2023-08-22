plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "nl.jochem"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    //json config things
    implementation("com.google.code.gson:gson:2.8.6")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}