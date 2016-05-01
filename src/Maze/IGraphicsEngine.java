package Maze;

import java.util.ArrayList;

import Maze.Characters.Character;

public interface IGraphicsEngine {

    void start();
    void pause();
    void resume();
    void finish();
    void drawColorScreen(int color);
    void drawExit();
    void setGameObjects(ArrayList<Object> gameObjects);
    void setCamera(Character.FACING direction);
}