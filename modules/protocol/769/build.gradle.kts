plugins {
    kotlin("jvm")
}

group = "net.candlemc.protocol"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:protocol:base"))
    implementation(project(":modules:base:types"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}