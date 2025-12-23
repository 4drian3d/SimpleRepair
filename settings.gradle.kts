@file:Suppress("UnstableApiUsage")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SimpleRepair"

pluginManagement {
//    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("fabric-loom") version "1.14.10"
    id("org.spongepowered.gradle.plugin") version "2.3.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

arrayOf(
    "common",
    "paper",
    "fabric",
    "sponge"
).forEach {
    include("simplerepair-$it")
    project(":simplerepair-$it").projectDir = file(it)
}