// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Include the `app` and `utils` subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html

rootProject.name = "Candle"
file("modules").walk().filter { dir ->
    dir.isDirectory &&
            (dir.resolve("build.gradle.kts").exists() || dir.resolve("build.gradle").exists())
}.forEach { moduleDir ->
    // Compute the relative path from the "modules" folder and normalize file separators
    val relativePath = moduleDir.relativeTo(rootDir.resolve("modules")).invariantSeparatorsPath
    // Convert the relative path to a Gradle project path (using ':' as separators)
    val gradleProjectPath = relativePath.split("/").joinToString(separator = ":", prefix = ":")
    include(gradleProjectPath)
    project(gradleProjectPath).projectDir = moduleDir
}