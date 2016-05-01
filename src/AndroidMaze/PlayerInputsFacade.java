package AndroidMaze;

import Maze.GameEngine;

public class PlayerInputsFacade { 
	
	private SensorMovement _sensorMovement;

	public PlayerInputsFacade(GameEngine gameEngine){
		_sensorMovement = new SensorMovement(gameEngine);
	}

	public void move(int sensorX, int sensorY){
		if (sensorX != 0)
			_sensorMovement.moveX(sensorX);
		else if (sensorY != 0)
			_sensorMovement.moveY(sensorY);
	}

}
