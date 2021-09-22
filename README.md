# Relativitization
Grand strategy game / agent-based simulation framework / social model in 4D spacetime

# Project layout
* `universe` core stuffs for the universe simulation
* `universe-server` server for running universe, depends on `universe`
* `universe-client` should be used with a gui, store non-gui functions, depends on `universe`
* `gdx-core` libgdx gui core, depends on `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)
* `gdx-desktop` libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)
* `gdx-android` libgdx gui android launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)


# Before working with the gui
There should be a `../relativitization-art/assets` directory

# Run desktop application
`./gradlew :gdx-desktop:run`

# Build fat jar
`./gradlew :gdx-desktop:fatJar`

# Build Android (debug) apk
`./gradlew :gdx-android:assembleDebug`

# Generate html doc
`./gradlew dokkaHtml`
