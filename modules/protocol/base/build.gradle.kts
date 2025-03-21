plugins {
    kotlin("jvm")
}

group = "net.candlemc.protocol"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:base:types"))
    implementation(project(":modules:base:event"))
    implementation(project(":modules:base:logger"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}