plugins {
    kotlin("jvm")
}

group = "net.candlemc.types"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:base:event"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}