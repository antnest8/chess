package dataaccess;

import model.AuthData;


import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> data;

    public MemoryAuthDAO(){
        data = new HashMap<String, AuthData>();
    }

    public void clear(){
        data.clear();
    }
    public boolean authValid(String authToken){
        return data.containsKey(authToken);
    }
    public void deleteAuth(String authToken){
        data.remove(authToken);
    }
    public void addAuth(AuthData authData){
        data.put(authData.authToken(), authData);
    }
}
