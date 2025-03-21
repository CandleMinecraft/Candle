plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":base:event"))
    implementation(project(":base:logger"))
    implementation(project(":base:types"))

    implementation(project(":protocol:base"))
    implementation(project(":protocol:769"))

    testImplementation(kotlin("test"))
}

application {
    mainClass = "net.candlemc.candle.app.AppKt"
}