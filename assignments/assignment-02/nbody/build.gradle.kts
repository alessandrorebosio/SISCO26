import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    java
    application
    checkstyle
    pmd
    jacoco
    id("com.github.spotbugs") version "6.5.13"
}

group = "it.unibo.sisco"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

// `./gradlew run` starts the simulation without GUI.
application {
    mainClass = "it.unibo.sisco.SimulationMain"
}

// `./gradlew runGui` starts the simulation with the Swing GUI.
tasks.register<JavaExec>("runGui") {
    group = "application"
    description = "Runs the simulation with the GUI."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "it.unibo.sisco.SimulationMainWithGUI"
    javaLauncher = javaToolchains.launcherFor(java.toolchain)
}

val spotbugsVersion = "4.10.5"

dependencies {
    // Annotations to suppress SpotBugs warnings (e.g. @SuppressFBWarnings)
    compileOnly("com.github.spotbugs:spotbugs-annotations:$spotbugsVersion")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Compiler: every warning (raw types, unchecked casts, ...) is an error.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

// Javadoc of public/protected production code is checked at compile time as well.
tasks.compileJava {
    options.compilerArgs.add("-Xdoclint:all/protected")
}

tasks.javadoc {
    (options as CoreJavadocOptions).addStringOption("Xwerror", "-quiet")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// Static analysis: configuration files live in config/ and every violation fails the build.
checkstyle {
    toolVersion = "14.4.0"
    configDirectory = layout.projectDirectory.dir("config/checkstyle")
    isIgnoreFailures = false
    maxWarnings = 0
}

pmd {
    toolVersion = "7.28.0"
    isConsoleOutput = true
    isIgnoreFailures = false
    ruleSets = listOf()
    ruleSetConfig = resources.text.fromFile("config/pmd/pmd.xml")
}

spotbugs {
    toolVersion = spotbugsVersion
    effort = Effort.MAX
    reportLevel = Confidence.LOW
    ignoreFailures = false
    excludeFilter = layout.projectDirectory.file("config/spotbugs/spotbugs-excludes.xml")
}

jacoco {
    toolVersion = "0.8.16"
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}
