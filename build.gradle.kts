import gg.essential.gradle.util.noRunConfigs

plugins {
    id("java")
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

val isLegacy = project.name == "legacy"

group = "com.github.stivais"
version = "${project.findProperty("version") as String}${if (isLegacy) "-legacy" else ""}"
val versionString = version.toString()

loom {
    noRunConfigs()
}

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net")
}

val embed: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    embed("com.mojang:brigadier:1.2.9")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "Commodore"
            groupId = project.group as String
            version = versionString
            artifact(tasks.jar.get().archiveFile)
        }
    }
    repositories {
        mavenLocal()
    }
}

java {
    withSourcesJar()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xlambdas=class")
    }
}


tasks {
    jar {
        archiveBaseName.set("Commodore")
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations = listOf(embed)
        mergeServiceFiles()
    }
}

preprocess {
    vars.put("LEGACY", if (isLegacy) 1 else 0)
    vars.put("!LEGACY", if (isLegacy) 0 else 1)
}