plugins {
    java
}

allprojects {
    apply<JavaPlugin>()
    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(25)
        }
    }
    java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}