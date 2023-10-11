import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(projects.simplerepairCommon)
    compileOnly(libs.slf4j)
}

sponge {
    apiVersion("10.0.0")
    license("GPL-3")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("simplerepair") {
        displayName("SimpleRepair")
        entrypoint("io.github._4drian3d.simplerepair.sponge.SimpleRepair")
        description(project.description)
        links {
            homepage("https://github.com/4drian3d/SimpleRepair")
            source("https://github.com/4drian3d/SimpleRepair")
            issues("https://github.com/4drian3d/SimpleRepair/issues")
        }
        contributor("4drian3d") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set("${rootProject.name}-Sponge")
        archiveClassifier.set("")
        doLast {
            copy {
                from(archiveFile)
                into("${rootProject.projectDir}/build")
            }
        }
    }
}