package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void createGame(GameData data);
    public GameData getGame(int gameID);
    public void updateGame(GameData data);
    public Collection<GameData> listGames();
    public void clear();
    public void deleteGame(int gameID);
}
