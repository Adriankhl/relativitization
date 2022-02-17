# Relativitization

Turn-based strategy game / agent-based simulation framework / social model in a 4D relativistic spacetime.

* [Buy the game from itch.io](https://adriankhl.itch.io/relativitization)
* [See the game documentation](https://github.com/Adriankhl/relativitization-game-doc) if you want to know more about
  the game
* [See the framework documentation](https://github.com/Adriankhl/relativitization-framework-doc) if you want to
  implement your model or alternative game mechanicsk

## Table of Contents

1. [Project layout](#project-layout)
2. [Introduction](#introduction)
3. [Run simulations](#run-simulations)
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
* `gdx-desktop`: libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`,
  and `universe-server`
* `gdx-android`: libgdx gui android launcher, depends on `gdx-core`, `universe`,  `universe-client`,
  and `universe-server`

## Introduction

Our interstellar future is always an interesting scenario to think about. There are great imaginations about all kinds
of possible interstellar society in books, movies, and games. Unfortunately, while the space computer games are fun to
play, the essential physics - relativity, is often missing.

Relativitization is an attempt to create a turn-based strategy / simulation game that respect special relativity. Since
building an academic social model can be quite similar to designing mechanisms in a simulation / strategy game, this can
also be used as an agent-based simulation framework for social scientists to build interstellar social models.

The source code is licensed under GPLv3, you are free to modify things in the `universe` to build your model, and you
can simulate your model via a terminal. Typically, this should be sufficient for academic studies.

To encourage people to support this project financially, the assets of the game are not opened. You need
to [buy the assets](https://adriankhl.itch.io/relativitization) to actually play the game.

Under specific circumstances, such as for educational purposes, or if you are a programmer who would like to contribute,
it may be possible to get free download-keys for the game. Please email your request
to [relativitization@gmail.com](relativitization@gmail.com).

## Run simulations

The following assumes you are using a Linux terminal. If you are working with Windows, you need to use the
Windows-equivalent commands, such as changing `./gradlew` to `gradlew.bat`.

You can run simulations of your model on the command line. This is an example model:
`./simulations/src/main/kotlin/relativitization/game/TypicalGame.kt`.

You can run the main function by:

```
./gradlew :simulations:run -PmainClass=relativitization.game.TypicalGameKt
```

Note that the main class has an additional `Kt` after the file name in Kotlin convention.

The default configuration can take up to 50% of the ram in your PC. If you think it is too little or too much,
open `./simulations/build.gradle.kts` and modify the line
`applicationDefaultJvmArgs = listOf("-XX:MaxRAMPercentage=50")`.

## Build the game

### Prerequisite

It is recommended to use jdk 17 to build and run the game.

Create a `../relativitization-art` directory. Get the `relativitization-assets.zip`
from [itch.io](https://adriankhl.itch.io/relativitization), unzip it and copy the
`assets` directory into `../relativitization-art`.

Alternatively, create `../relativitization-art/assets`, and copy these directories:

* `fonts`
* `images`
* `music`
* `skin`
* `sounds`
* `translations`

from your game into `../relativitization-art/assets`.

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

### Build Android (debug) apk

This will produce a `relativitization-debug.apk` in `gdx-android/build/outputs/apk/debug`:

```
./gradlew :gdx-android:assembleDebug
```

## Generate documentation

This will produce documentation html pages in `build/dokka/htmlMultiModule`:

```
./gradlew dokkaHtmlMultimodule
```

## Contribute

If you encounter a bug, please check to see if there is any existing issue before reporting the bug.

If you want to propose your ideas about the game, please use
[relativitization-game-doc](https://github.com/Adriankhl/relativitization-game-doc) instead.

Pull requests are welcome. To leave the possibility of changing the license of this project open, you are required to
sign a contributor license agreement to indicate that your contribution is under the MIT license. The instruction will
appear after your pull request has been created, just copy and paste a sentence in the comment.

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

Contributions from pull requests are licensed under the [MIT License](./CLALICENSE.md), see
the [Contributor License Agreement](./CLA.md).
