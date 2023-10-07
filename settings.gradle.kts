@file:Suppress("UnstableApiUsage")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SimpleRepair"

pluginManagement {
//    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
//        mavenCentral()
        maven("https://maven.fabricmc.net/")
//        maven("https://maven.quiltmc.org/repository/release/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

plugins {
    id("fabric-loom") version "1.4.1"
    id("org.spongepowered.gradle.plugin") version "2.1.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
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