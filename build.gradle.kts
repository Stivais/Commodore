plugins {
    id("gg.essential.loom") version "0.10.0.+"
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
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    implementation(kotlin("stdlib-jdk8"))
    implementation("com.mojang:brigadier:1.0.18")
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
