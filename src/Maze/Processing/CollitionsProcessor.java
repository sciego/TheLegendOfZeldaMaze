package Maze.Processing;

import java.util.ArrayList;

import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Objects.GameObject;
import Maze.Objects.Square;
import Maze.Objects.Wall;

public class CollitionsProcessor {
    // Fields
    private ArrayList<Character> _characters;
    private ArrayList<Wall> _walls;
    private ArrayList<Square> _squares;

    // Properties
    public ArrayList<Wall> getWalls(){
        return _walls;
    }
    public Wall getWall(int index){
        if (index >= _walls.size())
            return null;
        return _walls.get(index);
    }
    public ArrayList<Character> getCharacters(){
        return _characters;
    }
    public Character getCharacter(int index){
        if (index >= _characters.size())
            return null;
        return _characters.get(index);
    }

    // Constructor
    public CollitionsProcessor(ArrayList<Character> characters, ArrayList<Wall> walls, ArrayList<Square> squares){
        _characters = characters;
        _walls = walls;
        _squares = squares;
    }

    // Methods
    public boolean gameObjectsIntersectedInX(GameObject a, GameObject b){
        float axw = a.getLocationX() + a.getWidth();
        float bxw = b.getLocationX() + b.getWidth();

        if (a.getLocationX() > b.getLocationX() && a.getLocationX() < bxw
            || axw > b.getLocationX() && axw < bxw
            || a.getLocationX() <= b.getLocationX() && axw >= bxw){
            return true;
        }
        
        return false;
    }

    public boolean gameObjectsIntersectedInY(GameObject a, GameObject b){
        float ayh = a.getLocationY() + a.getHeight();
        float byh = b.getLocationY() + b.getHeight();

        if (a.getLocationY() > b.getLocationY() && a.getLocationY() < byh
            || ayh > b.getLocationY() && ayh < byh
            || a.getLocationY() <= b.getLocationY() && ayh >= byh){
            return true;
        }
        
        return false;
    }

    // Return the index of the Wall that got hit, in order to rellocate the GameObject according to the Wall's coordenates.
    // "nl" is the New Location of "go" is trying to get
    public int checkForWalls(GameObject go, FACING destination, float nl){
        int i, start = 0;
        Wall w = null;

        switch (destination) {
            case EAST:
            {
                float xw = nl + go.getWidth();
                for (i = start; i < _walls.size(); i++){
                    w = _walls.get(i);
                    if (!w.isEnabled())
                        continue;
                    if (go.getFloor() == w.getFloor() && xw > w.getLocationX() && xw < w.getLocationX() + w.getWidth() && gameObjectsIntersectedInY(go, w))
                        return i;
                }
                break;
            }
            case WEST:
            {
                for (i = start; i < _walls.size(); i++){
                    w = _walls.get(i);
                    if (!w.isEnabled())
                        continue;
                    if (go.getFloor() == w.getFloor() && nl < w.getLocationX() + w.getWidth() && nl > w.getLocationX() && gameObjectsIntersectedInY(go, w))
                        return i;
                }
                break;
            }
            case NORTH:
            {
                for (i = start; i < _walls.size(); i++){
                    w = _walls.get(i);
                    if (!w.isEnabled())
                        continue;
                    if (go.getFloor() == w.getFloor() && nl < w.getLocationY() + w.getHeight() && nl > w.getLocationY() && gameObjectsIntersectedInX(go, w))
                        return i;
                }
                break;
            }
            case SOUTH:
            {
                float yh = nl + go.getHeight();
                for (i = start; i < _walls.size(); i++){
                    w = _walls.get(i);
                    if (!w.isEnabled())
                        continue;
                    if (go.getFloor() == w.getFloor() && yh > w.getLocationY() && yh < w.getLocationY() + w.getHeight() && gameObjectsIntersectedInX(go, w))
                        return i;
                }
                break;
            }
        }
        
        return -1;
    }

    // "seeker" could reach to "target" without collididing with a Wall
    public boolean gameObjectReachable(Character seeker, GameObject target, FACING seekerDirection){
        float sxw = seeker.getLocationX() + seeker.getWidth();
        float syh = seeker.getLocationY() + seeker.getHeight();
        float txw = target.getLocationX() + target.getWidth();
        float tyh = target.getLocationY() + target.getHeight();
        int i, j;

        switch (seekerDirection){
            case NORTH:
            {
                if (gameObjectsIntersectedInX(target, seeker) == false || seeker.getLocationY() < target.getLocationY())
                    return false;
                j = (int) (seeker.getLocationY() - tyh);
                for (i = 1; i <= j; i++){
                    if (checkForWalls(seeker, FACING.NORTH, seeker.getLocationY()-i) != -1)
                        return false;
                }
                return true;
            }
            case SOUTH:
            {
                if (gameObjectsIntersectedInX(target, seeker) == false || seeker.getLocationY() > target.getLocationY() + target.getHeight())
                    return false;
                j = (int) (target.getLocationY() - syh);
                for (i = 1; i <= j; i++){
                    if (checkForWalls(seeker, FACING.SOUTH, seeker.getLocationY()+i) != -1)
                        return false;
                }
                return true;
            }
            case EAST:
            {
                if (gameObjectsIntersectedInY(target, seeker) == false || seeker.getLocationX() > target.getLocationX() + target.getWidth())
                    return false;
                j = (int) (target.getLocationX() - sxw);
                for (i = 1; i <= j; i++){
                    if (checkForWalls(seeker, FACING.EAST, seeker.getLocationX()+i) != -1)
                        return false;
                }
                return true;
            }
            case WEST:
            {
                if (gameObjectsIntersectedInY(target, seeker) == false || seeker.getLocationX() < target.getLocationX())
                    return false;
                j = (int) (seeker.getLocationX() - txw);
                for (i = 1; i <= j; i++){
                    if (checkForWalls(seeker, FACING.WEST, seeker.getLocationX()-i) != -1)
                        return false;
                }
                return true;
            }
        }

        return false;
    }

    public int checkForCharacters(Character seeker, FACING direction, float nl){
        float cxw, cyh;
        int i;

        switch (direction){
            case NORTH:
            {
                for (i = 0; i < _characters.size(); i++){
                    cyh = _characters.get(i).getLocationY() + _characters.get(i).getHeight();
                    if (seeker != _characters.get(i) && gameObjectsIntersectedInX(seeker, _characters.get(i)) && nl < cyh && nl > _characters.get(i).getLocationY())
                        return i;
                }
                break;
            }
            case SOUTH:
            {
                nl += seeker.getHeight();
                for (i = 0; i < _characters.size(); i++){
                    cyh = _characters.get(i).getLocationY() + _characters.get(i).getHeight();
                    if (seeker != _characters.get(i) && gameObjectsIntersectedInX(seeker, _characters.get(i)) && nl > _characters.get(i).getLocationY() && nl < cyh)
                        return i;
                }
                break;
            }
            case EAST:
            {
                nl += seeker.getWidth();
                for (i = 0; i < _characters.size(); i++){
                    cxw = _characters.get(i).getLocationX() + _characters.get(i).getWidth();
                    if (seeker != _characters.get(i) && gameObjectsIntersectedInY(seeker, _characters.get(i)) && nl > _characters.get(i).getLocationX() && nl < cxw)
                        return i;
                }
                break;
            }
            case WEST:
            {
                for (i = 0; i < _characters.size(); i++){
                    cxw = _characters.get(i).getLocationX() + _characters.get(i).getWidth();
                    if (seeker != _characters.get(i) && gameObjectsIntersectedInY(seeker, _characters.get(i)) && nl < cxw && nl > _characters.get(i).getLocationX())
                        return i;
                }
                break;
            }
        }
        

        return -1;
    }

    public int targetInAttackRange(Character seeker, GameObject target){
        float txw = target.getLocationX() + target.getWidth();
        float tyh = target.getLocationY() + target.getHeight();
        
        if (gameObjectsIntersectedInX(target, seeker)){
            float syr = seeker.getLocationY() - seeker.getAttackRange();
            if (syr <= tyh && syr >= target.getLocationY() && gameObjectReachable(seeker, target, FACING.NORTH))
                return 1;
            float syhr = seeker.getLocationY() + seeker.getHeight() + seeker.getAttackRange();
            if (syhr >= target.getLocationY() && syhr <= tyh && gameObjectReachable(seeker, target, FACING.SOUTH))
                return 2;
        }
        else if (gameObjectsIntersectedInY(target, seeker)){
            float sxwr = seeker.getLocationX() + seeker.getWidth() + seeker.getAttackRange();
            if (sxwr >= target.getLocationX() && sxwr <= txw && gameObjectReachable(seeker, target, FACING.EAST))
                return 3;
            float sxr = seeker.getLocationX() - seeker.getAttackRange();
            if (sxr <= txw && sxr >= target.getLocationX() && gameObjectReachable(seeker, target, FACING.WEST))
                return 4;
        }

        return 0;
    }
    
    // Bridge related:
    private boolean characterCouldMoveInX(Character character, Square s){
        float cxw = character.getLocationX() + character.getWidth();
        float cyh = character.getLocationY() + character.getHeight();
        float sxw = s.getLocationX() + s.getWidth();
        float syh = s.getLocationY() + s.getHeight();

        if (character.getLocationY() >= s.getLocationY() && cyh <= syh){
            if (character.getLocationX() >= s.getLocationX() && character.getLocationX() <= sxw || cxw >= s.getLocationX() && cxw <= sxw)
                return true;
        }

        return false;
    }

    private boolean characterCouldMoveInY(Character character, Square s){
        float cxw = character.getLocationX() + character.getWidth();
        float cyh = character.getLocationY() + character.getHeight();
        float sxw = s.getLocationX() + s.getWidth();
        float syh = s.getLocationY() + s.getHeight();

        if (character.getLocationX() >= s.getLocationX() && cxw <= sxw){
            if (character.getLocationY() >= s.getLocationY() && character.getLocationY() <= syh || cyh >= s.getLocationY() && cyh <= syh)
                return true;
        }

        return false;
    }

    public boolean checkForBridges(Character character, FACING direction, float nl){
        switch (direction){
            case EAST:
            {
                for (Square s : _squares){
                    if (character.getFloor() == s.getFloor() && characterCouldMoveInX(character, s) && s.Right() != null){
                        if (nl + character.getWidth() >= s.Right().getLocationX())
                            character.setFloor(s.Right().getFloor());
                        return true;
                    }
                }
                break;
            }
            case WEST:
            {
                for (Square s : _squares){
                    if (character.getFloor() == s.getFloor() && characterCouldMoveInX(character, s) && s.Left() != null){
                        if (nl <= s.Left().getLocationX() + s.Left().getWidth())
                            character.setFloor(s.Left().getFloor());
                        return true;
                    }
                }
                break;
            }
            case NORTH:
            {
                for (Square s : _squares){
                    if (character.getFloor() == s.getFloor() && characterCouldMoveInY(character, s) && s.Up() != null){
                        if (nl <= s.Up().getLocationY() + s.Up().getHeight())
                            character.setFloor(s.Up().getFloor());
                        return true;
                    }
                }
                break;
            }
            case SOUTH:
            {
                for (Square s : _squares){
                    if (character.getFloor() == s.getFloor() && characterCouldMoveInY(character, s) && s.Down() != null){
                        if (nl + character.getHeight() >= s.Down().getLocationY())
                            character.setFloor(s.Down().getFloor());
                        return true;
                    }
                }
                break;
            }
        }

        return false;
    }

    public boolean characterCollidedWithGameObject(Character character, GameObject go){
        if (go.getFloor() != character.getFloor())
            return false;

        if (gameObjectsIntersectedInX(character, go) && gameObjectsIntersectedInY(character, go))
            return true;
    
        return false;
    }

    
}
