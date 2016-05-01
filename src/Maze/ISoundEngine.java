package Maze;

public interface ISoundEngine {
    public enum Sound {
        LINK_SWORD1,
        LINK_SWORD2,
        LINK_SWORD3,
        LINK_SHIELD,
        LINK_HURT,
        LINK_KILLED,
        SWORD,
        RUPEE,
        CUT_BUSH,
        SECRET,
        DEKU_NUT,
        ENEMY_PAWN,
        ENEMY_GUARD,
        ENEMY_KILLED,
        BGM_DEFAULT,
        BGM_BATTLE,
        GAME_OVER,
        WALK_GRASS,
        WALK_BRIDGE,
        HEART,
        BGM_GANON,
        GANON_ATTACK_FIRE,
        GANON_ATTACK_BALL,
        GANON_ATTACK_BALL_HIT,
        GANON_ATTACK_BOUNCE,
        GANON_TELEPORT,
        GANON_KILLED,
        ENDING
    }

    void playSound(Sound sound);
    void pause();
    void resume();
    void finish();
    void stopSound(Sound sound);
}
