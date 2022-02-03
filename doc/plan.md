# Keep it simple

* Only one dimension in resource quality
* Beside factory and research, other pop functionalities depend on size only.
* Only adult population
* No migration
* No work at other carrier
* No minimum wage and unemployment benefit
* Unused centralization
* No pop buy resource from other player
* No alliance

# Current unused data

* Size of factory and institute
* Open and close factory
* Size of carrier
* Child and elder population
* Quality 2 and 3 of resources
* Reputation of research institute and laboratory
* Centralization
* Ally
* Player specific tariff
* Fuel tariff

# Technical TODO List

* Optimize afterimage and load saved universe to reduce memory usage [x]

# Mechanism TODO list

* Basic resource output from stellar system [x]
* Pop update daily need [x]
* Salary, and allocate pop to factory, institute and lab [x]
* Pop buy resource to fulfill need [x]
* Pop growth: Medic [x]
* Education level: educator [x]
* Entertainment production: entertainer [x]
* Tax and tariff, send to leaders [x]
* Research [x]
* How research affect product [x]
* Export center [x]
* Basic diplomacy [x]
* Merge carrier [x]
* New carrier [x]
* New player [x]
* Basic military: Soldier [x]
* Sync leader data to subordinate, e.g., enemy [x]
* Sync economy data to subordinate [x]
* Stop war after long period of time [x]
* Adjust attack and export by time dilation [x]
* Limit fuel produced in a single unit space cube to prevent unlimited population [x]
* Adjust the price and quality bound based on overall desire and trade [x]
* Attack spend production fuel

# Default AI TODO List

* AI state store last command to subordinate [x]
* Review resource factory construction after desire change [x]
* Decrease salary and increase price when pop saving is too much compare to storage [x]

# Command TODO List

* Destroy factory [x]
* Send fuel to foreign factory [x]
* Change economy policy, e.g., tax, storage [x]
* Change politics data [x]
* Change salary [x]
* Build institute and laboratory [x]
* Improve diplomatic relation by sending resource [x]
* Declare war, declare independence [x]
* Merge direct subordinate at exactly the same position [x]
* Open and close factories [x]

# Generate universe TODO List

* One stellar per player generation [x]
* Generate proper knowledge network [x]

# GUI TODO List

* Map mode [x]
* Show execute warning [x]
* Filter resource factory by resource type [x]
* Default build factory and institute employee to population, research equipment to production [x]
* Rotation based on next position [x]

# Control TODO List

* Fuel and resource weight instead of target [x]

# Test TODO List

* Declare war [x]
* Combat [x]
* Propose peace [x]
* New player [x]
* Merge player [x]

# Future TODO List

* Optimize UI, only change what is shown, separate stage and use camera instead of computing the zoom manually
* Science era, e.g., Biology era favour nation with more biologist
* Transfer player and transfer carrier commands
* Alliance
* Retract or transfer subordinate
* Decrease relation when a factory is removed
* Physics: time dilation of photon rocket, momentum conservation when sending fuel (transfer opposite momentum by
  virtual photon?)
* GUI: ai-relevant data