# Relativitization
Turn-based strategy game / agent-based simulation framework / social model in 4D relativistic spacetime.

## Table of Contents
1. [Project layout](#project-layout)
2. [Build the game](#build-the-game)
3. [Run simulation](#run-simulation)
4. [Generate documentation](#generate-documentation)

## Project layout
* `universe` core to run universe simulations
* `universe-server` server to run the game, depends on `universe`
* `universe-client` store non-gui functions that are useful to game client, depends on `universe`
* `gdx-core` libgdx gui core, depends on `universe` and  `universe-client`
* `gdx-desktop` libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server`
* `gdx-android` libgdx gui android launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server`

## Build the game
The following assumes you are using a Linux terminal. If you are working with Windows, you need to use the Windows-equivalent commands, such as changing `./gradlew` to `gradlew.bat`.

### Prerequisite
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

### Run desktop application
In your terminal, run:
```
./gradlew :gdx-desktop:run
```

### Build fat jar for desktop
This will produce a `Relativitization.jar` in `../relativitization-art/assets`:
```
./gradlew :gdx-desktop:fatJar
```

### Build executable for linux
Go to `../relativitization-art` (`cd ../relativitization-art`), then run
```
jpackage --input ./assets --name relativitization-linux --main-jar Relativitization.jar --type app-image --java-options XX:MaxRAMPercentage=80
```

The generated `relativitization-linux` folder contains a `bin/relativitization-linux` executable.

### Build executable for Windows on Linux
You need `wine` to cross-build Windows executable on Linux.

Download Windows jdk at `../windows/jdk/jdk-17`

Go to `relativitization-art` if it is not your current directory, then run
```
wine ../windows/jdk/jdk-17/bin/jpackage.exe --input ./assets --dest C:/relativitization-output --name relativitization-win --main-jar Relativitization.jar --type app-image --java-options XX:MaxRAMPercentage=80
```

The generated `~/.wine/drive_c/relativitization-output/relativitization-win` folder contains a `relativitization-win.exe` executable.


### Build Android (debug) apk
This will produce a `relativitization-debug.apk` in `gdx-android/build/outputs/apk/debug`: 
```
./gradlew :gdx-android:assembleDebug
```

## Run simulation
Here is an example:
`./simulations/src/main/kotlin/relativitization/abm/AllDefault.kt`.

You can run the main function by:
```
./gradlew :simulation:run -PmainClass=relativitization.abm.AllDefaultKt
```

Note that the main class has an additional `Kt` after the file name in kotlin convention.

## Generate documentation
This will produce documentation pages in `build/dokka/htmlMultiModule`:
```
./gradlew dokkaHtmlMultimodule
```