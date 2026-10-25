package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clear();
    public boolean authValid();
    public void deleteAuth(String username);
    public void addAuth(AuthData data);
}
