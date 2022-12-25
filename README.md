# Relativitization

Turn-based strategy game / agent-based simulation framework / social model in 4D, relativistic
spacetime.

* [Buy the game from itch.io](https://adriankhl.itch.io/relativitization)
* [See the game documentation](https://github.com/Adriankhl/relativitization-game-doc) if you want
  to know more about the game
* [See the framework documentation](https://github.com/Adriankhl/relativitization-framework-doc) if
  you want to implement your model or alternative game mechanics

## Table of Contents

1. [Project layout](#project-layout)
2. [Introduction](#introduction)
3. [Build the game](#build-the-game)
4. [Run simulations](#run-simulations)
5. [Generate documentation](#generate-documentation)
6. [Contribute](#contribute)
7. [License](#license)
8. [Citations](#citations)

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

Our interstellar future is always an interesting scenario to think about. There are great
imaginations about all kinds of possible interstellar societies in books, movies, and games.
Unfortunately, while the space computer games are fun to play, the essential physics - relativity,
is often missing.
This project, Relativitization, is an attempt to create a turn-based strategy / simulation game that
obeys special relativity.

Besides being a playable game, this is also a flexible computational framework
which helps people to build other games or agent-based models on top of the framework.
For example, social scientists can utilize the framework to create interstellar social models.
Let's see if simulations can give us meaningful insights.

## Build the game

The following assumes you are using a Linux terminal. If you are working with Windows, you need to
use the Windows-equivalent commands, such as changing `./gradlew` to `gradlew.bat`.

### Prerequisite

It is recommended to use jdk 17 to build and run the game.

First create a directory to hold everything related to Relativitization:

```
mkdir relativitization-project
```

Navigate into the directory, clone this project:

```
cd relativitization-project
git clone https://github.com/Adriankhl/relativitization.git
```

Create a `relativitization-art` directory and download the
[game assets](https://filedn.com/lT8KEAGhB7RXdUYN4ykED5Y/relativitization-assets/assets.zip) to the directory:

```
mkdir relativitization-art
wget -P relativitization-art https://filedn.com/lT8KEAGhB7RXdUYN4ykED5Y/relativitization-assets/assets.zip
```

Extract the zip file to `./relativitization-art/assets`:
```
7z x relativitization-art/assets.zip -orelativitization-art/
```

You should have:

* `./relativitization-art/assets/fonts`
* `./relativitization-art/assets/images`
* `./relativitization-art/assets/license`
* ...

Now, navigate into `./relativitization` and you are ready to build the game:

```
cd relativitization
```

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

### Build Android apk

This will produce a `relativitization-free-standalone.apk`
in `gdx-android/build/outputs/apk/free/standalone`:

```
./gradlew :gdx-android:assembleStandalone
```

## Run simulations

You can create your own model and run it on your command line. This is an simple example:
`./simulations/src/main/kotlin/relativitization/game/TypicalGame.kt`.

Run the main function with 2 active processor and 25% maximum ram usage:

```
./gradlew :simulations:run -PmainClass=relativitization.game.TypicalGameKt -PprocessorCount=2 -PramPercentage=25
```

Note that the main class has an additional `Kt` after the file name in Kotlin's convention.

## Generate documentation

This will produce html documentation pages to `build/dokka/htmlMultiModule`:

```
./gradlew dokkaHtmlMultiModule
```

## Contribute

If you encounter a bug, please check to see if there is any existing issue before reporting the bug.

If you want to propose your ideas about the game, please use
[relativitization-game-doc](https://github.com/Adriankhl/relativitization-game-doc) instead.

Pull requests are welcome. To leave the possibility of changing the license of this project open,
you are required to sign a contributor license agreement to indicate that your contribution is under
the MIT license. The instruction will appear after your pull request has been created, just copy and
paste a sentence in the comment.

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

## Citations

If you use this framework in a publication, please cite:

```
@software{relativitization,
  author       = {Lai, Kwun Hang},
  title        = {Relativitization},
  year         = 2022,
  doi          = {10.5281/zenodo.6120765},
  url          = {https://doi.org/10.5281/zenodo.6120765},
  howpublished = {https://github.com/Adriankhl/relativitization},
}
```

Also consider citing
this [paper](https://arxiv.org/abs/2206.11019):

```
@article{lai2022social,
  title={On social simulation in 4D relativistic spacetime},
  author={Lai, Kwun Hang},
  journal={arXiv preprint arXiv:2206.11019},
  year={2022}
}
```
