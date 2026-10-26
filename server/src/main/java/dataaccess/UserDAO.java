package dataaccess;

import model.LoginAttempt;
import model.UserData;

public interface UserDAO {
    void clear();
    void createUser(UserData user) throws DataAccessException;
    //public UserData getUser(String name) throws DataAccessException;
    boolean userPassCorrect(LoginAttempt login);
    boolean userExists(String name);
}
