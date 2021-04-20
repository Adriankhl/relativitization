# Game flow
* Universe server modify players' data based on game mechanism
* Universe server execute commands based on the space-time interval, the generate commands are store
* Universe client check if the universe server is ready, if ready, download 3D view
* Universe server run the ai to determine command list
* Universe client render the gui based on the 3D View
* Universe client decide the command list (also the one from events, if not decided, the default command is executed)
* Universe client send the command list to the universe server
* Universe server execute the self-command and neighbor-command, others are stored
* Universe server convert and store the new player data 