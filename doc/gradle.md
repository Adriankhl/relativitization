# Tasks

## Test

Clean and test:

```shell
./gradlew clean test
```

## Desktop

Run application:

```shell
./gradlew :gdx-desktop:run
```

Build fat jar:

```shell
./gradlew :gdx-desktop:fatJar
```

## Android

Standalone apk:

```shell
./gradlew :gdx-android:assembleStandalone
```

Release aab:

```shell
./gradlew :gdx-android:bundleRelease
```

## Publish core library

Maven local:

```shell
./gradlew publishToMavenLocal
```

Maven central:

```shell
./gradlew :universe-core:publishMavenPublicationToOSSRHRepository
```

## Publish game

Package desktop and android:

```shell
./gradlew cleanAll packageAll
```