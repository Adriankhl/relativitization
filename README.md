# Relativitization
Turn-based strategy game / agent-based simulation framework / social model in a 4D relativistic spacetime.

**Status of the project**: Command line simulation is functioning, the first version of the game
will be released in February 2022.

## Table of Contents
1. [Project layout](#project-layout)
2. [Introduction and references](#introduction-and-references)
3. [Run simulation](#run-simulation)
4. [Build the game](#build-the-game)
5. [Generate documentation](#generate-documentation)
6. [Contribute](#contribute)
7. [License](#license)

## Project layout
* `universe`: core of this project
* `simulations`: model simulation examples, depends on `universe`
* `universe-server`: server to run the game, depends on `universe`
* `universe-client`: store non-gui functions that are useful to game client, depends on `universe`
* `gdx-core`: libgdx gui core, depends on `universe` and  `universe-client`
* `gdx-desktop`: libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server`
* `gdx-android`: libgdx gui android launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server`

## Introduction and references
Our interstellar future is always an interesting scenario to think about. 
There are great imaginations about all kinds of possible interstellar society in books, movies, and games.
Unfortunately, while computer games are fun to play, it is rare to see the essential physics - relativity, in games.

Relativitization is an attempt to create a turn-based strategy / simulation game that respect special relativity.
Since building an academic social model can be quite similar to designing mechanisms in a simulation / strategy game,
this can also be used as an agent-based simulation framework for 
social scientists to build social models.

The source code is licensed under GPLv3, you are free to modify or add things in the `universe` to
build your model, and you can simulate your model using terminal.

To encourage people to support this project financially, the assets of the game are not opened.
You need to buy the assets to play the game, though you are free to modify the game logic to experiment and share
your ideas.

## Run simulation
The following assumes you are using a Linux terminal.
If you are working with Windows, you need to use the Windows-equivalent commands, 
such as changing `./gradlew` to `gradlew.bat`.

You can run simulations of your model on the command line. Here is an example: 
`./simulations/src/main/kotlin/relativitization/game/TypicalGame.kt`.

You can run the main function by:
```
./gradlew :simulations:run -PmainClass=relativitization.game.TypicalGameKt
```

Note that the main class has an additional `Kt` after the file name in Kotlin convention.

## Build the game

### Prerequisite
You need jdk 17 to build the game.

There should be a `../relativitization-art/assets` directory. If not, create `../relativitization-art/assets`
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
jpackage --input ./assets --name relativitization-linux --main-jar Relativitization.jar --type app-image --java-options XX:MaxRAMPercentage=60
```

The generated `relativitization-linux` folder contains a `bin/relativitization-linux` executable.

### Build executable for Windows on Linux
You need `wine` to cross-build Windows executable on Linux.

Download Windows jdk at `../windows/jdk/jdk-17`

Go to `relativitization-art` if it is not your current directory, then run
```
wine ../windows/jdk/jdk-17/bin/jpackage.exe --input ./assets --dest C:/relativitization-output --name relativitization-win --main-jar Relativitization.jar --type app-image --java-options XX:MaxRAMPercentage=60
```

The generated `~/.wine/drive_c/relativitization-output/relativitization-win` folder contains a `relativitization-win.exe` executable.


### Build Android (debug) apk
This will produce a `relativitization-debug.apk` in `gdx-android/build/outputs/apk/debug`: 
```
./gradlew :gdx-android:assembleDebug
```

## Generate documentation
This will produce documentation pages in `build/dokka/htmlMultiModule`:
```
./gradlew dokkaHtmlMultimodule
```

## Contribute
If you encounter a bug, please check to see if there is any existing issue before reporting the bug.

If you want to propose your idea on the game, please use
[relativitization-game-doc](https://github.com/Adriankhl/relativitization-game-doc) instead.

Pull requests are welcome. Since it is not entirely clear whether the GPLv3 license is the best option for
this project, to leave the possibility of changing the license in the future open,
you are required to sign a contributor license agreement to indicate that your contribution is under the MIT license.
You just need to copy and paste a sentence as a comment in your pull request, 
the instruction will appear after your pull request has been created.

## License
The source code of Relativitization is licensed under the [GPLv3 License](./LICENSE.md).

        Relativitization
        Copyright (C) 2021-2022  Lai Kwun Hang

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.

Contributions from pull requests are licensed under the [MIT License](./CLALICENSE.md),
see the [Contributor License Agreement](./CLA.md).
