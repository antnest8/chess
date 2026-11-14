package dataaccess.sql;

import chess.ChessBoard;
import chess.ChessGame;
import com.sun.jdi.InternalException;
import dataaccess.GameDAO;
import dataaccess.exceptions.DataAccessException;
import model.BadDataRequest;
import model.GameData;
import model.SavedGame;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

public class DBGameDAO implements GameDAO {
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
            game_id INT NOT NULL,
            current_turn CHAR(5) NOT NULL,
            board TEXT NOT NULL,
            PRIMARY KEY(game_id)
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
                try(var preppedObjState = conn.prepareStatement("INSERT INTO gameobj(game_id, current_turn, board) VALUES (?, ?, ?);")){
                    preppedObjState.setInt(1, id);
                    preppedObjState.setString(2, "WHITE");
                    ChessBoard board = new ChessBoard();
                    board.resetBoard();
                    preppedObjState.setString(3, board.toString());
                    preppedObjState.executeUpdate();
                }
                return id;
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: game aggregation internal error.");
        }
    }

    public GameData getGame(int gameID){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement(
                    "SELECT game.white_user, game.black_user, game.name, gameobj.current_turn, gameobj.board " +
                            "FROM game, gameobj WHERE game.id = ? AND gameobj.game_id = game.id;")){
                preppedState.setInt(1, gameID);
                try(var rs = preppedState.executeQuery()){
                    if(rs.next()) {
                        return parseSQLGame(rs, gameID);
                    }else{
                        throw new BadDataRequest("Error: Game not found!");
                    }
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: game retrieval internal error.");
        }
    }

    public void updateGame(GameData data){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("UPDATE game SET white_user=?, black_user=?, name=? WHERE id=?")){
                if(data.whiteUsername() == null){
                    preppedState.setNull(1, Types.VARCHAR);
                }else{
                    preppedState.setString(1, data.whiteUsername());
                }
                if(data.blackUsername() == null){
                    preppedState.setNull(2, Types.VARCHAR);
                }else{
                    preppedState.setString(2, data.blackUsername());
                }
                preppedState.setString(3, data.gameName());
                preppedState.setInt(4, data.gameID());
                preppedState.executeUpdate();
            }
        }catch (DataAccessException | SQLException ex){
            throw new InternalException("Error: internal game fetch error");
        }
    }

    public Collection<GameData> listGames(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement(
                    "SELECT game.id, game.white_user, game.black_user, game.name, gameobj.current_turn, gameobj.board" +
                            " FROM game, gameobj WHERE gameobj.game_id =  game.id;")){
                try(var rs = preppedState.executeQuery()){
                    Collection<GameData> gameList = new ArrayList<GameData>();
                    while(rs.next()) {
                        gameList.add(parseSQLGame(rs));
                    }
                    return gameList;
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: game retrieval internal error.");
        }
    }

    public void clear(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE game, gameobj;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: cannot clear game data.");
        }
    }

    void configureDB(){
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                try (var preppedState = conn.prepareStatement(GAME_SCHEMA)) {
                    preppedState.executeUpdate();
                }

                try (var preppedState = conn.prepareStatement(GAMEOBJ_SCHEMA)) {
                    preppedState.executeUpdate();
                }

            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: configuration Error");
        }
    }

    private GameData parseSQLGame(ResultSet rs, int gameID)throws SQLException{
        return new GameData(gameID,
                rs.getString("white_user"),
                rs.getString("black_user"),
                rs.getString("name"),
                new SavedGame(rs.getString("current_turn"), rs.getString("board")));
    }

    private GameData parseSQLGame(ResultSet rs)throws SQLException{
        return new GameData(rs.getInt("id"),
                rs.getString("white_user"),
                rs.getString("black_user"),
                rs.getString("name"),
                new SavedGame(rs.getString("current_turn"), rs.getString("board")));
    }
}
