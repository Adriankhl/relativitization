# Data structure of the universe
* Almost all data has an immutable version and mutable version
* Exceptions:
  * Command: always immutable
  * Event: always immutable
  * UniverseState: always mutable
  * UniverseData: mutable to allow update, but it mostly consists of immutable components