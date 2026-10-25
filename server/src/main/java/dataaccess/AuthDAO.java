package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clear();
    public boolean authValid(String authToken);
    public void deleteAuth(String authToken);
    public void addAuth(AuthData data);
    public AuthData getAuth(String authToken);
}
