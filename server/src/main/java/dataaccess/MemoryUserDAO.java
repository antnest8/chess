package dataaccess;

import model.LoginAttempt;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private Map<String, UserData> data;

    public MemoryUserDAO(){
        this.data = new HashMap<String, UserData>();
    }

    public void clear(){
        data.clear();
    }
    public void createUser(UserData userData) throws DataAccessException {
        if(data.putIfAbsent(userData.username(), userData) != null){
            throw new DataAccessException("Error: Username already exists");
        }
    }
    public boolean userPassCorrect(LoginAttempt login){
        return userExists(login.username()) && data.get(login.username()).password().equals(login.password());
    }
    public boolean userExists(String name){
        return data.containsKey(name);
    }
}
