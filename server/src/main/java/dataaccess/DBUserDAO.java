package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.LoginAttempt;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class DBUserDAO {
    private static final String USER_SCHEMA = """
            CREATE TABLE IF NOT EXISTS user(
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY(username)
            );
            """;

    DBUserDAO() throws DataAccessException {
        configureDB();
    }

    void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE user;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: cannot clear user data.");
        }
    }
    void createUser(UserData user) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("INSERT INTO user(username, password, email) VALUES (?,?,?);")){
                preppedState.setString(1, user.username());
                preppedState.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                preppedState.setString(3, user.email());
                preppedState.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException("Error: user aggregation internal error.");
        }
    }
    boolean userPassCorrect(LoginAttempt login) throws DataAccessException{

    }
    boolean userExists(String name) throws DataAccessException{

    }

    private void configureDB() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement(USER_SCHEMA)){
                preppedState.executeUpdate();
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: configuration Error");
        }
    }
    }
}
