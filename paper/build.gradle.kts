plugins {
    alias(libs.plugins.runpaper)
    alias(libs.plugins.pluginyml.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.paper)
    implementation(projects.itemrepairCommon)
}

tasks {
    clean {
        // Deletes the directory that is generated by the runPaper plugin
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
        minecraftVersion("1.20.1")
    }
}

paper {
    name = "ItemRepair"
    main = "io.github._4drian3d.simplerepair.paper.SimpleRepair"
    loader = "io.github._4drian3d.simplerepair.paper.RepairLoader"
    authors = listOf("4drian3d")

    foliaSupported = true
    apiVersion = "1.20"
}