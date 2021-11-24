# Client post to server
* Create universe: (server) setUniverse, (client) postNewUniverse, postLoadUniverse
* Register player: (server) registerPlayer, (client) postRegisterPlayer
* Modify server setting: (server) setUniverseServerSettings, (client) httpPostUniverseServerSettings
* Run universe: (server) runUniverse, (client) httpPostRunUniverse
* Stop universe: (server) stopUniverse, (client) httpPostStopUniverse
* Human input: (server) humanInput, (client) httpPostHumanInput
* Stop waiting for input: (server) isServerWaitingInput.set, (client) httpPostStopWaiting

# Client get from server
* Universe status: (server) getUniverseStatusMessage, (client) getUniverseStatusMessage
* Get id data: (server) getAvailableIdList, getAvailableHumanIdList, (client) getAvailableIdList, getAvailableHumanIdList
* Get saved universe: (server) getSavedUniverse(), (client) httpGetSavedUniverse
* Check if the player is dead: (server) isPlayerDead, (client) httpGetCheckIsPlayerDead
* Get universe 3D data: (server) getUniverseData3D, (client) httpGetUniverseData3D