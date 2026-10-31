package dataaccess;

import com.sun.jdi.InternalException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;

import java.sql.SQLException;

public class DBAuthDAO implements AuthDAO{

    private static final String AUTH_SCHEMA = """
        CREATE TABLE IF NOT EXISTS auth(
            token CHAR(36) NOT NULL ,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (token)
        );
""";

    public DBAuthDAO(){
        configureDB();
    }

    public void clear(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE auth;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: internal data error while clearing.");
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
            throw new InternalException("Error: auth aggregation internal error.");
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT token, username FROM auth WHERE token = ?;")){
                preppedState.setString(1, authToken);
                try(var rs = preppedState.executeQuery()){
                    rs.next();
                    return new AuthData(rs.getString("username"), rs.getString("token"));
                }
            }
        }catch (SQLException ex){
            throw new InternalException("Error: auth retrieval internal error.");
        }
    }

    public void deleteAuth(String authToken){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("DELETE FROM auth WHERE token=?;")){
                preppedState.setString(1, authToken);
                preppedState.executeUpdate();
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: auth deletion internal error.");
        }
    }

    //we are going to do this instead of just selecting the row because it doesn't send unnecessary -
    // - user data to the service layer.
    public boolean authValid(String authToken){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM auth WHERE token=?);")){
                preppedState.setString(1, authToken);
                try(var rs = preppedState.executeQuery()){
                    rs.next();
                    return rs.getBoolean(1);
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: auth validation internal error");
        }
    }

    private void configureDB(){
    try{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement(AUTH_SCHEMA)){
                preppedState.executeUpdate();
            }
        }
    }catch (SQLException | DataAccessException ex){
        System.err.println("auth database config failed. reason: " + ex.getMessage());
            throw new InternalException("Error: configuration Error");
        }
    }
}
