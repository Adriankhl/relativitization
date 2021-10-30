# Relativitization
Grand strategy game / agent-based simulation framework / social model in 4D spacetime

# Project layout
* `universe` core stuffs for the universe simulation
* `universe-server` server for running universe, depends on `universe`
* `universe-client` should be used with a gui, store non-gui functions, depends on `universe`
* `gdx-core` libgdx gui core, depends on `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)
* `gdx-desktop` libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)
* `gdx-android` libgdx gui android launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)


# Build the game
## Prerequisite
You need jdk 17 to build the game.

There should be a `../relativitization-art/assets` directory. If not, create `../relativitization-art`
and copy the assets:
* `fonts`
* `images`
* `music`
* `skin`
* `sounds`
* `translations`
from the game into the `../relativitization-art/assets` folder.

## Run desktop application
`./gradlew :gdx-desktop:run`

## Build fat jar for desktop
This will produce a `Relativitization.jar` in `../relativitization-art/assets`:
`./gradlew :gdx-desktop:fatJar`

## Build executable for linux
Go to `../relativitization-art` (`cd ../relativitization-art`), then run
```
jpackage --input ./assets --name relativitization-linux --main-jar Relativitization.jar --type app-image
```

The generated `relativitization-art/relativitization-linux` folder contains a `bin/relativitization-linux`
executable.

## Build executable for Windows on Linux
Download Windows jdk at `windows/jdk/jdk-17`

Go to `../relativitization-art` (`cd ../relativitization-art`), then run
```
wine ../windows/jdk/jdk-17/bin/jpackage.exe --input ./assets --dest C:/relativitization-output --name relativitization-win --main-jar Relativitization.jar --type app-image
```

The generated `~/.wine/drive_c/relativitization-output/relativitization-win` folder contains a `relativitization-win.exe`
executable.


## Build Android (debug) apk
`./gradlew :gdx-android:assembleDebug`

# Generate html doc
`./gradlew dokkaHtml`
