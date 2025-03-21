plugins {
    kotlin("jvm")
}

group = "net.candlemc.types"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":base:event"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}