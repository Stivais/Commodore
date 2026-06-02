plugins {
    id("java")
    kotlin("jvm") version "2.2.0"
    `maven-publish`
}

group = "com.github.stivais"
version = project.findProperty("version") as String

val versionString = version.toString()

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    compileOnly("com.mojang:brigadier:1.0.18")
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
    }
}