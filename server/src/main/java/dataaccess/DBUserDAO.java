package dataaccess;

import com.sun.jdi.InternalException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UsernameTakenException;
import model.LoginAttempt;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class DBUserDAO implements UserDAO{
    private static final String USER_SCHEMA = """
            CREATE TABLE IF NOT EXISTS user(
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY(username)
            );
            """;

    public DBUserDAO() {
        configureDB();
    }

    public void clear(){
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("TRUNCATE TABLE user;")){
                preppedState.executeUpdate();
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: cannot clear user data.");
        }
    }
    public void createUser(UserData user) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            if(!userExists(user.username())) {
                try (var preppedState = conn.prepareStatement("INSERT INTO user(username, password, email) VALUES (?,?,?);")) {
                    preppedState.setString(1, user.username());
                    preppedState.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                    preppedState.setString(3, user.email());
                    preppedState.executeUpdate();
                }
            }else{
                throw new UsernameTakenException("Error: This username is taken");
            }
        }catch(UsernameTakenException ex){
            throw ex;
        } catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: user aggregation internal error.");
        }
    }
    public boolean userPassCorrect(LoginAttempt login) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT password FROM user WHERE username = ?;")){
                preppedState.setString(1, login.username());
                try(var rs = preppedState.executeQuery()){
                    if(rs.next()) {
                        return BCrypt.checkpw(login.password(), rs.getString("password"));
                    }else{
                        return false;
                    }
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: login check internal error.");
        }
    }

    public boolean userExists(String name) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            try(var preppedState = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM user WHERE username=?);")){
                preppedState.setString(1, name);
                try(var rs = preppedState.executeQuery()){
                    rs.next();
                    return rs.getBoolean(1);
                }
            }
        }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: auth validation internal error");
        }
    }

    private void configureDB() {
    try{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preppedState = conn.prepareStatement(USER_SCHEMA)) {
                preppedState.executeUpdate();
            }
        }
    }catch (SQLException | DataAccessException ex){
            throw new InternalException("Error: configuration Error");
        }
    }

}
