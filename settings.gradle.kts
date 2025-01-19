pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }
    plugins {
        kotlin("jvm") version "2.0.0"

        val egtVersion = "0.6.5"
        id("gg.essential.multi-version.root") version egtVersion
    }
}

listOf(
    "legacy",
    "modern"
//    "1.8.9-fabric",
//    "1.20.4-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}

rootProject.name = "Commodore"
rootProject.buildFileName = "root.gradle.kts"

