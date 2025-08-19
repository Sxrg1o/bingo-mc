plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
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
    implementation("de.rapha149.signgui:signgui:2.5.4")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    relocate("de.rapha149.signgui", "com.bingaso.bingo.lib.signgui")

    // (Optional) Set the name of the final JAR file
    archiveClassifier.set("") // This removes the '-all' suffix from the jar name
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
