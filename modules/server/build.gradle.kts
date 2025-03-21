plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

dependencies {
    implementation(project(":modules:base:event"))
    implementation(project(":modules:base:logger"))
    implementation(project(":modules:base:types"))

    implementation(project(":modules:protocol:base"))
    implementation(project(":modules:protocol:769"))

    testImplementation(kotlin("test"))
}

application {
    mainClass = "net.candlemc.candle.app.AppKt"
}