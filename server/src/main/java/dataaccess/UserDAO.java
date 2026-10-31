package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UsernameTakenException;
import model.LoginAttempt;
import model.UserData;

public interface UserDAO {
    void clear();
    void createUser(UserData user) throws UsernameTakenException;
    boolean userPassCorrect(LoginAttempt login);
    boolean userExists(String name);
}
