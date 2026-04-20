import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.thm.asc.tiel.interpreter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("info.picocli:picocli:4.7.7")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("tiel")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "de.thm.asc.tiel.interpreter.Main"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}