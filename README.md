# Relativitization
Grand strategy game in 4D spacetime

# Project layout
* `universe` core stuffs for the universe simulation
* `universe-server` server for running universe, depends on `universe`
* `universe-client` should be used with a gui, store non-gui functions, depends on `universe`
* `gdx-core` libgdx gui core, depends on `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)
* `gdx-desktop` libgdx gui desktop launcher, depends on `gdx-core`, `universe`,  `universe-client`, and `universe-server` (very loosely, only manage the server `start()` and `stop()`)


# Run desktop application
Make sure `../relativitization-art/assets` exists, then:

`./gradlew :gdx-desktop:run`