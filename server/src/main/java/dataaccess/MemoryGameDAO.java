package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    Map<Integer, GameData> data;

    public MemoryGameDAO(){
        this.data = new HashMap<Integer, GameData>();
    }

    public void createGame(GameData game){
        data.put(game.gameID(), game);
    }
    public GameData getGame(int gameID){
        return data.get(gameID);
    }
    public void updateGame(GameData game){
        data.put(game.gameID(), game);
    }
    public Collection<GameData> listGames(){
        return data.values();
    }
    public void clear(){
        data.clear();
    }
    public void deleteGame(int gameID){
        data.remove(gameID);
    }
}
