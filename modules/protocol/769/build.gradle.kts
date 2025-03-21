plugins {
    kotlin("jvm")
}

group = "net.candlemc.protocol"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":protocol:base"))
    implementation(project(":base:types"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}