package dataaccess;

import model.UserData;

public interface UserDAO {
    public void clear();
    public void createUser(UserData user) throws DataAccessException;
    public UserData getUser(String name) throws DataAccessException;
    public boolean userExists(String name);
}
