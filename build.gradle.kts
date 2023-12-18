plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
}

group = "com.github.stivais"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("com.mojang:brigadier:1.0.18")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.stivais"
            artifactId = "Commodore"
            version = "1.0.0"
            from(getComponents().getByName("java"))
        }
    }
}