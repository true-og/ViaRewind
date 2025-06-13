rootProject.name = "viarewind"

includeBuild("build-logic")

fun setupViaSubproject(name: String) {
    val pName = "viarewind-$name"
    include(pName)
    project(":$pName").projectDir = file(name)
}

setupViaSubproject("common")
setupViaSubproject("bukkit")

