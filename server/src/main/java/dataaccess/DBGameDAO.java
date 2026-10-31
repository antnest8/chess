package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DBGameDAO implements GameDAO{
    private static final String GAME_SCHEMA = """
            CREATE TABLE IF NOT EXISTS game(
            id INT NOT NULL AUTO_INCREMENT,
            white_user VARCHAR(255),
            black_user VARCHAR(255),
            name VARCHAR(255) NOT NULL,
            PRIMARY KEY(id)
            );
            """;

    //probably implement this completely later
    private static final String GAMEOBJ_SCHEMA = """
            CREATE TABLE IF NOT EXISTS gameobj(
            current_turn CHAR(5) NOT NULL,
            board TEXT NOT NULL,
            );
            """;

    public DBGameDAO(){
        configureDB();
    }

    public int createGame(String name){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("INSERT INTO game(name) VALUES (?);", Statement.RETURN_GENERATED_KEYS)){
                preppedState.setString(1, name);
                preppedState.executeUpdate();

                int id = 0;
                var resultSet = preppedState.getGeneratedKeys();
                if(resultSet.next()){
                    id = resultSet.getInt(1);
                }
                return id;
            }
        }catch (SQLException | DataAccessException ex){
            throw new RuntimeException("Error: game aggregation internal error.");
        }
    }

    public GameData getGame(int gameID){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT white_user, black_user, name FROM game WHERE id = ?;")){
                preppedState.setInt(1, gameID);
                try(var rs = preppedState.executeQuery()){
                    if(rs.next()) {
                        return parseSQLGame(rs, gameID);
                    }else{
                        throw new NullPointerException("Error: Game not found!");
                    }
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new RuntimeException("Error: game retrieval internal error.");
        }
    }

    public void updateGame(GameData data){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("UPDATE game(white_user, black_user, name) VALUES (?,?,?) WHERE id=?")){
                preppedState.setString(1, !data.whiteUsername().isBlank() ? data.whiteUsername() : null);
                preppedState.setString(2, !data.blackUsername().isBlank() ? data.blackUsername() : null);
                preppedState.setString(3, data.gameName());
                preppedState.setInt(4, data.gameID());
            }
        }catch (DataAccessException | SQLException ex){
            throw new RuntimeException("Error: internal game fetch error");
        }
    }

    public Collection<GameData> listGames(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT id, white_user, black_user, name FROM game;")){
                try(var rs = preppedState.executeQuery()){
                    Collection<GameData> gameList = new ArrayList<GameData>();
                    while(rs.next()) {
                        gameList.add(parseSQLGame(rs));
                    }
                    return gameList;
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new RuntimeException("Error: game retrieval internal error.");
        }
    }

    public void clear(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE game;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException | DataAccessException ex){
            throw new RuntimeException("Error: cannot clear game data.");
        }
    }

    void configureDB(){
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                try (var preppedState = conn.prepareStatement(GAME_SCHEMA)) {
                    preppedState.executeUpdate();
                }
                /* --note: this is for the future--
                try (var preppedState = conn.prepareStatement(GAMEOBJ_SCHEMA)) {
                    preppedState.executeUpdate();
                }
                */
            }
        }catch (SQLException | DataAccessException ex){
            System.err.println("database configuration failed. Reason: " + ex.getMessage());
            throw new RuntimeException("Error: configuration Error");
        }
    }

    private GameData parseSQLGame(ResultSet rs, int gameID)throws SQLException{
        return new GameData(gameID,
                rs.getString("white_user"),
                rs.getString("black_user"),
                rs.getString("name"),
                null);
    }

    private GameData parseSQLGame(ResultSet rs)throws SQLException{
        return new GameData(rs.getInt("id"),
                rs.getString("white_user"),
                rs.getString("black_user"),
                rs.getString("name"),
                null);
    }
}
