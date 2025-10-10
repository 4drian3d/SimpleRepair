plugins {
    alias(libs.plugins.runpaper)
    alias(libs.plugins.pluginyml.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.paper.api)
    implementation(projects.simplerepairCommon)
}

tasks {
    clean {
        delete("run")
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set("${rootProject.name}-Paper")
        archiveClassifier.set("")
        doLast {
            copy {
                from(archiveFile)
                into("${rootProject.projectDir}/build")
            }
        }
    }
    runServer {
        minecraftVersion("1.21.10")
    }
}

paper {
    name = "SimpleRepair"
    main = "io.github._4drian3d.simplerepair.paper.SimpleRepair"
    loader = "io.github._4drian3d.simplerepair.paper.RepairLoader"
    bootstrapper = "io.github._4drian3d.simplerepair.paper.RepairBootstrap"
    authors = listOf("4drian3d")

    foliaSupported = true
    apiVersion = "1.21"
}