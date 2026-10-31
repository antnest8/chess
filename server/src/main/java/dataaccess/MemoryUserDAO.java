package dataaccess;

import dataaccess.exceptions.UsernameTakenException;
import model.LoginAttempt;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private final Map<String, UserData> data;

    public MemoryUserDAO(){
        this.data = new HashMap<>();
    }

    public void clear(){
        data.clear();
    }
    public void createUser(UserData userData) throws UsernameTakenException {
        if(data.putIfAbsent(userData.username(), userData) != null){
            throw new UsernameTakenException("Error: Username already exists");
        }
    }
    public boolean userPassCorrect(LoginAttempt login){
        return userExists(login.username()) && data.get(login.username()).password().equals(login.password());
    }
    public boolean userExists(String name){
        return data.containsKey(name);
    }
}
