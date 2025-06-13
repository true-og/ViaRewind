plugins {
    id("vr.platform-conventions")
}

repositories {
    maven {
        name = "SpigotMC"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "SpongePowered"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    annotationProcessor("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}

val pluginVersion = project.version
val pluginDescription = project.description

tasks.named<ProcessResources>("processResources") {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to pluginVersion, "description" to pluginDescription))
    }
}

