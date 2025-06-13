plugins {
    id("vr.base-conventions")
    id("net.raphimc.class-token-replacer") version "1.0.0"
}

dependencies {
    compileOnly("io.netty:netty-all:4.0.20.Final")
    compileOnly("com.google.guava:guava:17.0")
}

val mavenVersion = rootProject.extra["maven_version"].toString()

val latestCommitHashValue = rootProject.providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.map(String::trim).get()

sourceSets {
    named("main") {
        (this as ExtensionAware).extensions.configure<Any>("classTokenReplacer") {
            (this as groovy.lang.GroovyObject).withGroovyBuilder {
                "property"("\${version}", mavenVersion)
                "property"(
                    "\${impl_version}",
                    "git-ViaRewind-$mavenVersion:$latestCommitHashValue"
                )
            }
        }
    }
}

val viaProxyConfiguration = configurations.create("viaProxy").apply {
    dependencies.add(
        project.dependencies.create("net.raphimc:ViaProxy:3.3.5-SNAPSHOT") {
            isTransitive = false
        }
    )
}

tasks.register<JavaExec>("runViaProxy") {
    dependsOn(tasks.named<Jar>("jar"))
    mainClass.set("net.raphimc.viaproxy.ViaProxy")
    workingDir = file("run")
    classpath = viaProxyConfiguration
    jvmArgs("-DskipUpdateCheck")
    if (System.getProperty("viaproxy.gui.autoStart") != null) {
        jvmArgs("-Dviaproxy.gui.autoStart")
    }
    doFirst {
        val jarsDir = file("$workingDir/jars").apply { mkdirs() }
        tasks.named<Jar>("jar").get().archiveFile.get().asFile.copyTo(
            target = file("$jarsDir/${project.name}.jar"),
            overwrite = true
        )
    }
    doLast {
        file("$workingDir/jars/${project.name}.jar").delete()
        file("$workingDir/logs").deleteRecursively()
    }
}

