package Maze.Processing;

import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Character.State;
import Maze.Characters.Moblin;

public class MoblinFightStrategy implements IAIStrategy{

    // Fields
    private Moblin _cpu;
    private Character _target;
    private CollitionsProcessor _collitionsProcessor;
    private Random _random;

    // Constructors
    public MoblinFightStrategy(Moblin seeker, Character target, CollitionsProcessor cp){
        _cpu = seeker;
        _target = target;
        _collitionsProcessor = cp;
        _random = new Random();
    }

    // Implemented methods
    @Override
    public State operate(){
        fight();
        if (_collitionsProcessor.targetInAttackRange(_cpu, _target) == 0)
            _cpu.changeState(State.STANDING);
        return _cpu.state();
    }

    // Methods
    private void fight(){
        if (_cpu.state() == State.FIGHTING && _random.nextInt(5) == 4)
            _cpu.changeState(State.ATTACKING);
    }

}
