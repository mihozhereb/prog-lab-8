plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "ru.mihozhereb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":common"))
}

dependencies {
    implementation("commons-codec:commons-codec:1.18.0")
}


tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes(
            mapOf(
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name },
                "Main-Class" to "ru.mihozhereb.Main"
            )
        )
    }
}