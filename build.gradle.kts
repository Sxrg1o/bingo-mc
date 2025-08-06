plugins {
    id("java")
}

// La información de tu proyecto
group = "com.bingaso.bingo"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}