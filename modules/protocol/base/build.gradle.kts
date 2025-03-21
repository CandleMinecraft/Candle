plugins {
    kotlin("jvm")
}

group = "net.candlemc.protocol"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":base:types"))
    implementation(project(":base:event"))
    implementation(project(":base:logger"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}