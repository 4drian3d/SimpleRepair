plugins {
    id("fabric-loom")
    alias(libs.plugins.shadow)
}

val shade: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.adventure.platform.fabric)

    shadeDependency(projects.simplerepairCommon)
    shadeDependency(libs.configurate.hocon)
    includeDependency(libs.luckopermissionsapi)
}

fun DependencyHandlerScope.shadeDependency(dependency: Any) {
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
        inputs.properties("version" to project.version)
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveFileName.set("SimpleRepair-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${project.rootDir}/build"))
    }
    shadowJar {
        from(sourceSets.main.get().output)
        from(sourceSets.named("client").get().output)
        configurations = listOf(shade)
        relocate("org.spongepowered.configurate", "io.github._4drian3d.simplerepair.libs.configurate")
        relocate("io.leangen.geantyref", "io.github._4drian3d.simplerepair.libs.geantyref")
        relocate("net.kyori.option", "io.github._4drian3d.simplerepair.libs.option")
    }
}

java {
    withSourcesJar()
}