import gg.essential.gradle.util.noRunConfigs

plugins {
    id("java")
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
    `maven-publish`
}

group = "com.github.stivais"
version = project.findProperty("version") as String

val isLegacy = project.name == "legacy"

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("com.mojang:brigadier:1.2.9")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = if (isLegacy) "Commodore-legacy" else "Commodore"
            groupId = project.group as String
            version = project.findProperty("version") as String
            artifact(tasks.jar.get().archiveFile)
        }
    }
    repositories {
        mavenLocal()
    }
}

java.withSourcesJar()
loom.noRunConfigs()

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xlambdas=class")
    }
}

preprocess {
    vars.put("LEGACY", if (isLegacy) 1 else 0)
    vars.put("!LEGACY", if (isLegacy) 0 else 1)
}