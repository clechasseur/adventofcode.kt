plugins {
    kotlin("jvm") version "1.5.10"
}

group = "io.github.clechasseur"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<Test> {
    testLogging {
        outputs.upToDateWhen { false }
        showStandardStreams = true
    }
}
