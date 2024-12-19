plugins {
    kotlin("jvm") version "1.3.72"
}

group = "org.clechasseur"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    testLogging {
        outputs.upToDateWhen { false }
        showStandardStreams = true
    }
}
