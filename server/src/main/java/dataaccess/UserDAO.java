package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.LoginAttempt;
import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    boolean userPassCorrect(LoginAttempt login) throws DataAccessException;
    boolean userExists(String name) throws DataAccessException;
}
