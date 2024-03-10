plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
}

group = "com.github.stivais"
version = project.findProperty("version") as String

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    api("com.mojang:brigadier:1.0.18")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.stivais"
            artifactId = "Commodore"
            version = project.findProperty("version") as String
            from(getComponents().getByName("java"))
        }
    }
}
