package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    boolean authValid(String authToken);
    void deleteAuth(String authToken);
    void addAuth(AuthData data);
    AuthData getAuth(String authToken);
}
