package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private Map<Integer, GameData> data;
    private int count;

    public MemoryGameDAO(){
        this.data = new HashMap<Integer, GameData>();
        this.count = 0;
    }

    public int createGame(String gameName){
        var game = new GameData(++count, null, null, gameName, null);
        data.put(game.gameID(), game);
        return game.gameID();
    }
    public GameData getGame(int gameID){
        GameData game = data.get(gameID);
        if(game == null) {throw new NullPointerException("No game found");}
        return game;
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
