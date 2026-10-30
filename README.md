# SISCO26

Repository for the Concurrent Systems course.

## Repository layout

```text
tools/
├── ltsa/                    # LTSA and the LTL2Buchi runtime
└── pipe/
    └── pipe.jar             # Self-contained PIPE runtime
scripts/
├── run-ltsa.sh / .bat       # Start LTSA
└── run-pipe.sh / .bat       # Start PIPE
assignments/
└── assignment-01/
    ├── Lift_system.lts      # FSP model for the lift system
    └── docs/                # Assignment report sources
```

Both tools require Java. On macOS/Linux, run:

```bash
./scripts/run-ltsa.sh
./scripts/run-pipe.sh
```

On Windows, run the corresponding `.bat` files. The scripts resolve their
paths relative to the repository, so they can be launched from any directory.

The assignment-specific documentation is available in
[`assignments/assignment-01/README.md`](assignments/assignment-01/README.md).