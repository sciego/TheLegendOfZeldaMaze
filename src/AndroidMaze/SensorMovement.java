package AndroidMaze;

import Maze.GameEngine;

class SensorMovement {
	private GameEngine _gameEngine;

	public SensorMovement(GameEngine gameEngine){
		_gameEngine = gameEngine;
	}

	public void moveX(int accuracy){
		if (_gameEngine != null && _gameEngine.isRunning()){
            if (accuracy > 0 && _gameEngine.movingPlayerLeft() == false){
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(true);
                _gameEngine.movePlayerUp(false);
            	_gameEngine.movePlayerDown(false);
            }
            else if (accuracy < 0 && _gameEngine.movingPlayerRight() == false){
                _gameEngine.movePlayerRight(true);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(false);
            	_gameEngine.movePlayerDown(false);
            }
    	}
	}

	public void moveY(int accuracy){
		if (_gameEngine != null && _gameEngine.isRunning()){
            if (accuracy > 0 && _gameEngine.movingPlayerDown() == false){
            	_gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(false);
                _gameEngine.movePlayerDown(true);
            }
            else if (accuracy < 0 && _gameEngine.movingPlayerUp() == false){
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(true);
                _gameEngine.movePlayerDown(false);
            }

        }
	}

}
