package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void clear();
    boolean authValid(String authToken);
    void deleteAuth(String authToken);
    void addAuth(AuthData data)throws DataAccessException;
    AuthData getAuth(String authToken)throws DataAccessException;
}
