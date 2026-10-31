package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class DBAuthDAO {

    private static final String AUTH_SCHEMA = """
        CREATE TABLE IF NOT EXISTS auth(
            token NOT NULL CHAR(36),
            username NOT NULL VARCHAR(255),
            PRIMARY KEY (token)
        );
""";

    DBAuthDAO() throws DataAccessException{
        configureDB();
    }

    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE auth;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: cannot clear auth data.");
        }
    }

    public void addAuth(AuthData auth) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("INSERT INTO auth(token, username) VALUES (?,?);")){
                preppedState.setString(1, auth.authToken());
                preppedState.setString(2, auth.username());
                preppedState.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: auth aggregation internal error.");
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT token, username FROM auth WHERE token = ?;")){
                preppedState.setString(1, authToken);
                try(var rs = preppedState.executeQuery()){
                    rs.next();
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: auth retrieval internal error.");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("DELETE FROM auth WHERE token=?;")){
                preppedState.setString(1, authToken);
                preppedState.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: auth deletion internal error.");
        }
    }

    //we are going to do this instead of just selecting the row because it doesn't send unnecessary -
    // - user data to the service layer.
    public boolean authValid(String authToken) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM auth WHERE token=?);")){
                preppedState.setString(1, authToken);
                try(var rs = preppedState.executeQuery()){
                    rs.next();
                    return rs.getBoolean(1);
                }
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: auth validation internal error");
        }
    }

    private void configureDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement(AUTH_SCHEMA)){
                preppedState.executeUpdate();
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: configuration Error");
        }
    }
}
