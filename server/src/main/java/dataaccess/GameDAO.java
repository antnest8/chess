package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String name);
    GameData getGame(int gameID);
    void updateGame(GameData data);
    Collection<GameData> listGames();
    void clear();
    //void deleteGame(int gameID);
}
