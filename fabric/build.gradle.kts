plugins {
    id("fabric-loom")
//    alias(libs.plugins.shadow)
}

//val shade: Configuration by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    includeDependency(libs.configurate)
    include(projects.simplerepairCommon)
}

//fun DependencyHandlerScope.shadeModule(module: ProjectDependency) {
//    shade(module)
//    implementation(module)
//}

fun DependencyHandlerScope.includeDependency(dependency: Any) {
    modImplementation(dependency)
    include(dependency)
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    remapJar {
//        inputFile.set(shadowJar.get().archiveFile)
        //inputs.files sourceSets.named("client").get()
//        inputs.files.files.add(sourceSets.named("client").get().java.singleFile)
        archiveFileName.set("SimpleRepair-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${project.rootDir}/build"))
    }
//    shadowJar {
//        configurations = listOf(shade)
//    }
}

loom {
    splitEnvironmentSourceSets()
    mods {
        register("simplerepair") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.named("client").get())
        }
    }
}

java {
    withSourcesJar()
}