package Maze;

public interface IGameCallback {
    public enum Message {
        WIN,
        LOSE,
        MOBLIN_APPEARED,
        EXIT_APPEARED,
        GANON_APPEARED,
        GANON_DEFEATED,
        NAVY_HINT
    }

    void endGame(Message result);
    void notify(Message alert);
}
