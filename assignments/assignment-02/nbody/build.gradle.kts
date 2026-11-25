import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

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

// `./gradlew run` opens the GUI, `./gradlew run --args=cli` runs on the terminal only.
application {
    mainClass = "it.unibo.sisco.SimulationMain"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

val spotbugsVersion = "4.10.5"

dependencies {
    // Annotations to suppress SpotBugs warnings (e.g. @SuppressFBWarnings)
    compileOnly("com.github.spotbugs:spotbugs-annotations:$spotbugsVersion")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Normal compilation only shows warnings, so `./gradlew run` works as long as the code compiles.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:all")
}

// Strict compilation, part of `check`/`build`: every javac warning (raw types, unchecked casts, ...)
// and every Javadoc problem on public/protected production code is an error.
// The output goes to build/lint and is never used to run the application.
fun registerLintCompile(name: String, sourceSetName: String, extraArgs: List<String>) =
    tasks.register<JavaCompile>(name) {
        val sourceSet = sourceSets[sourceSetName]
        description = "Compiles the $sourceSetName sources treating every warning as an error."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        source = sourceSet.java
        classpath = sourceSet.compileClasspath
        destinationDirectory = layout.buildDirectory.dir("lint/$sourceSetName")
        javaCompiler = javaToolchains.compilerFor(java.toolchain)
        options.compilerArgs.addAll(listOf("-Werror") + extraArgs)
    }

val lintMain = registerLintCompile("lintMain", "main", listOf("-Xdoclint:all/protected"))
val lintTest = registerLintCompile("lintTest", "test", emptyList())

tasks.check {
    dependsOn(lintMain, lintTest)
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
    // Failures are raised below, after the bugs have been printed on the console.
    ignoreFailures = true
    excludeFilter = layout.projectDirectory.file("config/spotbugs/spotbugs-excludes.xml")
}

// SpotBugs writes its findings only to report files: also print them on the console and fail if there are any.
tasks.withType<SpotBugsTask>().configureEach {
    val textReport = layout.buildDirectory.file("reports/spotbugs/$name.txt")
    reports.create("html")
    reports.create("text") {
        outputLocation = textReport
    }
    doLast {
        val bugs = textReport.get().asFile.takeIf { it.exists() }?.readLines()?.filter { it.isNotBlank() }.orEmpty()
        if (bugs.isNotEmpty()) {
            bugs.forEach { logger.error(it) }
            throw GradleException("SpotBugs found ${bugs.size} bug(s) in $name, see the list above.")
        }
    }
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
