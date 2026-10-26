package service;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UsernameTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class ServiceTests {
    private static ChessService svc;
    //common strings
    private final static String newUser = "{\"username\": \"username\",  \"password\": \"password\",  \"email\": \"email\"}";
    private final static String loginReq = "\"{\"username\": \"username\",  \"password\": \"password\"}";
    private final static String newGame = "{\"gameName\": \"gameName\"}";
    private final static String joinGameWhite = "{\"playerColor\": \"WHITE\",\"gameID\": 1}";
    private final static String joinGameBlack = "{\"playerColor\": \"BLACK\",\"gameID\": 1}";
    private AuthData existingAuth;

    @BeforeAll
    public static void init(){
        svc = new ChessService();
    }

    @BeforeEach
    public void setup(){
        svc.clear();

        try {
            existingAuth = new Gson().fromJson(svc.register("{\"username\": \"preUsername\",  \"password\": \"prePassword\",  \"email\": \"pre@email\"}"), AuthData.class);
        } catch (DataAccessException ex){
            Assertions.fail("Failure to register an existing user");
        }
    }

    @Test
    @DisplayName("Clear() works")
    public void clearTest(){
        svc.clear();
        try{
            AuthData newAuth = new Gson().fromJson(svc.register("{\"username\": \"preUsername\",  \"password\": \"prePassword\",  \"email\": \"pre@email\"}"), AuthData.class);
            Assertions.assertNotEquals(newAuth.authToken(), existingAuth.authToken());
        } catch (DataAccessException ex){
            Assertions.fail("Could not register existing user after clearing service. threw" + ex.getMessage());
        }
    }

    @Test
    @DisplayName("NewRegister Success")
    public void newRegisterTest(){
        try {
            AuthData newAuth = new Gson().fromJson(svc.register(newUser), AuthData.class);
        } catch (DataAccessException ex){
            Assertions.fail("Attempting to register a new user threw: " + ex.getMessage());
        }
    }

    @Test
    @DisplayName("Double Register Failure")
    public void doubleRegisterTest(){
        try {
            AuthData newAuth = new Gson().fromJson(svc.register(newUser), AuthData.class);
            Assertions.assertThrows(UsernameTakenException.class, () -> {svc.register(newUser);});
        }  catch (DataAccessException ex){
            Assertions.fail("Was expecting a UsernameTakenException not a: " + ex.getMessage());
        }
    }

    @Test
    @DisplayName("Register Bad Request")
    public void badRegisterTest(){
        Assertions.assertThrows(DataAccessException.class, () -> {svc.register("{\"username\": \"\",  \"password\": \"password\",  \"email\": \"email\"}");}, "permissive to blank username");
        Assertions.assertThrows(DataAccessException.class, () -> {svc.register("{\"username\": \"myUser\",  \"password\": \"password\"}");}, "permissive to null email");
    }
}
