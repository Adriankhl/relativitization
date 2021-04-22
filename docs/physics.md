# spacial, time, mass, and energy unit
* Fixed time per turn = 1 unit (T)
* Fixed mass 1 kg = 1 unit (M)
* Speed of light = c
* Spacial unit 1 T / c
* Energy unit
  * c = 1, 1 unit = 1kg * c^2 = 1
  * c != 1, 1 unit = c^2
  
# after image
* when player 1 move from (0, 0, 0) to (0, 0, 1), due to relativistic time delay, player 2 at (0, 0, 0) cannot see player 2 for some turns
* we solve this by forcing the after image of player 1 to stay at (0, 0, 0) for a while, the int4D.t is unchanged over time
  