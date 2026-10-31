package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void clear()throws DataAccessException;
    boolean authValid(String authToken)throws DataAccessException;
    void deleteAuth(String authToken)throws DataAccessException;
    void addAuth(AuthData data)throws DataAccessException;
    AuthData getAuth(String authToken)throws DataAccessException;
}
