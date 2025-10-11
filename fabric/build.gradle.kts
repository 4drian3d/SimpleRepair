plugins {
    id("fabric-loom")
    alias(libs.plugins.shadow)
}

val shade: Configuration by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    includeDependency(libs.configurate.hocon)
    includeDependency(libs.luckopermissionsapi)
    includeDependency(libs.adventure.platform.fabric)
    shadeModule(projects.simplerepairCommon)
}

fun DependencyHandlerScope.shadeModule(dependency: Any) {
    shade(dependency)
    implementation(dependency)
}

fun DependencyHandlerScope.includeDependency(dependency: Any) {
    modImplementation(dependency)
    include(dependency)
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("simplerepair") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("SimpleRepair-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${project.rootDir}/build"))
    }
    shadowJar {
        configurations = listOf(shade)
    }
}

java {
    withSourcesJar()
}