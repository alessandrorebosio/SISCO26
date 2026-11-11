# N-body simulation

Concurrent N-body simulation for assignment-02 of the Concurrent Systems
course. It is a Gradle project written in Java 21 and organised with the
Model-View-Controller pattern.

## Requirements

- A JDK 21. Gradle finds it automatically through its toolchain support.
- Nothing else: the Gradle wrapper (`gradlew`) downloads the right Gradle
  version on first use.

All commands below are run from this folder:

```bash
cd assignments/assignment-02/nbody
```

## Project layout

```text
nbody/
├── build.gradle.kts            # Build, compiler flags and static analysis
├── settings.gradle.kts
├── gradlew, gradlew.bat        # Gradle wrapper
├── config/                     # Rules of the static analysis tools
│   ├── checkstyle/
│   ├── pmd/
│   └── spotbugs/
└── src/
    ├── main/java/it/unibo/sisco/
    │   ├── model/              # Simulation state and logic
    │   ├── view/               # User interface
    │   └── controller/         # Glue between model and view
    └── test/java/it/unibo/sisco/
```

## Build and checks

Build the project, run the tests and all the checks:

```bash
./gradlew build
```

Show every failure instead of stopping at the first tool that fails:

```bash
./gradlew build --continue
```

Run only the checks, without packaging the jar:

```bash
./gradlew check
```

The build fails on:

- any compiler warning, such as raw types or unchecked casts
  (`-Xlint:all -Werror`);
- missing or malformed Javadoc on public and protected code
  (`-Xdoclint:all/protected`);
- any Checkstyle, PMD or SpotBugs violation.

### Single tasks

| Check | Command |
|---|---|
| Compiler (types and Javadoc) | `./gradlew compileJava` |
| Checkstyle | `./gradlew checkstyleMain` |
| PMD | `./gradlew pmdMain` |
| SpotBugs | `./gradlew spotbugsMain` |
| Unit tests (JUnit 5) | `./gradlew test` |
| Test coverage (JaCoCo) | `./gradlew test jacocoTestReport` |
| Javadoc HTML | `./gradlew javadoc` |

Checkstyle, PMD and SpotBugs also have a `…Test` variant
(for example `./gradlew checkstyleTest`) that checks the test sources.

`./gradlew javadoc` fails until the project contains at least one public
class.

## Reports

Errors are printed in the terminal with file and line. Full HTML reports are
written to `build/`:

| Report | Path |
|---|---|
| Checkstyle | `build/reports/checkstyle/main.html` |
| PMD | `build/reports/pmd/main.html` |
| SpotBugs | `build/reports/spotbugs/main.html` |
| Tests | `build/reports/tests/test/index.html` |
| Coverage | `build/reports/jacoco/test/html/index.html` |
| Javadoc | `build/docs/javadoc/index.html` |

On macOS a report can be opened from the terminal, for example:

```bash
open build/reports/spotbugs/main.html
```

## Static analysis rules

The Checkstyle, PMD and SpotBugs rules in `config/` are derived from the
default configuration of
[gradle-java-qa](https://github.com/DanySK/gradle-java-qa) by Danilo Pianini
(Apache License 2.0). They can be edited freely to fit this project.
