plugins {
    id("vr.base-conventions")
    id("eclipse")
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

base {
    archivesName.set("ViaRewind")
}

val publishInclude by configurations.creating

dependencies {
    publishInclude(project(":viarewind-common"))
    publishInclude(project(":viarewind-bukkit"))
}

val latestCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.map(String::trim)

val latestCommitMessage: Provider<String> = providers.exec {
    commandLine("git", "log", "-1", "--pretty=%B")
}.standardOutput.asText.map(String::trim)

val branchName: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText.map(String::trim)

val licenseSuffix = project.name.ifEmpty { rootProject.name }
val publishTrees = providers.provider {
    configurations["publishInclude"].files.map { zipTree(it) }
}

tasks.named<Jar>("jar") {
    dependsOn(configurations["publishInclude"])
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(publishTrees) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
    manifest {
        attributes(mapOf("paperweight-mappings-namespace" to "mojang"))
    }
    from("LICENSE") { rename { "${it}_${licenseSuffix}" } }
}

idea {
    module { excludeDirs.add(file("run")) }
}

