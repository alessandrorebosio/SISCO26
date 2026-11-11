# SISCO26

Repository for the Concurrent Systems course.

## Repository layout

```text
tools/
├── ltsa/                    # LTSA and the LTL2Buchi runtime
├── pipe/
│   └── pipe.jar             # Self-contained PIPE runtime
└── jpf/                     # Java PathFinder (jpf-core, prebuilt jars)
scripts/
├── run-ltsa.sh / .bat       # Start LTSA
├── run-pipe.sh / .bat       # Start PIPE
└── run-jpf.sh / .bat        # Run JPF on a .jpf configuration
assignments/
├── assignment-01/
│   ├── lift_system.lts      # FSP model for the lift system
│   ├── lift_petri_net.xml   # Petri net model for the lift system
│   └── docs/                # Assignment report sources
└── assignment-02/
    ├── README.md            # Assignment description and requirements
    ├── nbody/               # N-body simulation (Gradle, Java 21, MVC)
    └── docs/                # Assignment report sources
```

Both tools require Java. On macOS/Linux, run:

```bash
./scripts/run-ltsa.sh
./scripts/run-pipe.sh
```

On Windows, run the corresponding `.bat` files. The scripts resolve their
paths relative to the repository, so they can be launched from any directory.

Java PathFinder runs only on **Java 11**: it is built from the `master` branch
of [jpf-core](https://github.com/javapathfinder/jpf-core) (revision `c517709`).
Code checked with JPF must be compiled with `javac --release 11`. On macOS the
launcher finds a JDK 11 automatically (for example `brew install openjdk@11`);
otherwise set `JPF_JAVA_HOME` to a JDK 11 installation:

```bash
javac --release 11 -d out MyModel.java
./scripts/run-jpf.sh MyModel.jpf
```

The N-body simulation of assignment-02 is a Gradle project that uses Java 21
through a Gradle toolchain. Run it from its folder with the wrapper:

```bash
cd assignments/assignment-02/nbody
./gradlew build
```

The build fails on any compiler warning (`-Xlint:all -Werror`), on missing or
malformed Javadoc of public/protected code, and on Checkstyle, PMD or SpotBugs
violations. Their rules live in `assignments/assignment-02/nbody/config/` and
are derived from [gradle-java-qa](https://github.com/DanySK/gradle-java-qa).
JaCoCo writes the test coverage report to `build/reports/jacoco/`.

The assignment-specific documentation is available in:

- [`assignments/assignment-01/README.md`](assignments/assignment-01/README.md)
- [`assignments/assignment-02/README.md`](assignments/assignment-02/README.md)
