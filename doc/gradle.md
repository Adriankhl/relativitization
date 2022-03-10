# Tasks

## Test

Clean and test:

```
./gradlew clean test
```

## Simulation

Update `model-gitignore.txt`:

```
./gradlew updateModelGitignore
```

Generate base project:

```
./gradlew createModelBase
```

## Desktop

Run application:

```
./gradlew :gdx-desktop:run
```

Build fat jar:

```
./gradlew :gdx-desktop:fatJar
```

## Android

Standalone apk:

```
./gradlew :gdx-android:assembleStandalone
```

Release aab:

```
./gradlew :gdx-android:bundleRelease
```

## Publish

Package desktop and android:

```
./gradlew clean packageAll
```