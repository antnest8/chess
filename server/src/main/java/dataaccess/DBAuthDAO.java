package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

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
            throw new DataAccessException("Error: cannot clear auth data.");
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
