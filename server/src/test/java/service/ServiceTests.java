package service;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameSpaceTaken;
import dataaccess.LoginIncorrect;
import dataaccess.UsernameTakenException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {
    private static ChessService svc;
    //common strings
    private final static String NEW_USER = "{\"username\": \"username\",  \"password\": \"password\",  " +
                                                "\"email\": \"email\"}";
    private final static String LOGIN_REQ = "{\"username\": \"preUsername\",  \"password\": \"prePassword\"}";
    private final static String NEW_GAME = "{\"gameName\": \"gameName\"}";
    private final static String JOIN_GAME_WHITE = "{\"playerColor\": \"WHITE\",\"gameID\": 1}";
    private final static String JOIN_GAME_BLACK = "{\"playerColor\": \"BLACK\",\"gameID\": 1}";
    private AuthData existingAuth;

    @BeforeAll
    public static void init(){
        svc = new ChessService();
    }

    @BeforeEach
    public void setup(){
        svc.clear();

        try {
            existingAuth = new Gson().fromJson(svc.register("{\"username\": \"preUsername\",  " +
                    "\"password\": \"prePassword\",  \"email\": \"pre@email\"}"), AuthData.class);
        } catch (DataAccessException ex){
            Assertions.fail("Failure to register an existing user");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Clear() works")
    public void clearTest(){
        svc.clear();
        try{
            AuthData newAuth = new Gson().fromJson(svc.register("{\"username\": \"preUsername\"," +
                    "  \"password\": \"prePassword\",  \"email\": \"pre@email\"}"), AuthData.class);
            Assertions.assertNotEquals(newAuth.authToken(), existingAuth.authToken());
        } catch (DataAccessException ex){
            Assertions.fail("Could not register existing user after clearing service. threw" + ex.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("NewRegister Success")
    public void newRegisterTest(){
        try {
            svc.register(NEW_USER);
        } catch (DataAccessException ex){
            Assertions.fail("Attempting to register a new user threw: " + ex.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Double Register Failure")
    public void doubleRegisterTest(){
        try {
            svc.register(NEW_USER);
            Assertions.assertThrows(UsernameTakenException.class, () -> svc.register(NEW_USER));
        }  catch (DataAccessException ex){
            Assertions.fail("Was expecting a UsernameTakenException not a: " + ex.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Register Bad Request From Null")
    public void badRegisterTest(){
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"password\": \"password\",  \"email\": \"email\"}"),
                "permissive to null username");
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"username\": \"myUser\", \"email\": \"email\"}"),
                "permissive to null password");
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"username\": \"myUser\",  \"password\": \"password\"}"),
                "permissive to null email");
    }

    @Test
    @Order(5)
    @DisplayName("Register Bad Request From Null")
    public void badBlankRegisterTest(){
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"username\": \"\", \"password\": \"password\",  \"email\": \"email\"}"),
                "permissive to null username");
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"username\": \"myUser\", \"password\": \"\t\",  \"email\": \"email\"}"),
                "permissive to null password");
        Assertions.assertThrows(DataAccessException.class,
                () -> svc.register("{\"username\": \"myUser\", \"password\": \"password\",  \"email\": \" \"}"),
                "permissive to null email");
    }

    @Test
    @Order(6)
    @DisplayName("Login Success and Failure")
    public void loginTest(){

        Assertions.assertDoesNotThrow(() -> {svc.login(LOGIN_REQ);}, "Failed to login.");
        svc.clear();
        Assertions.assertThrows(LoginIncorrect.class, () -> svc.login(LOGIN_REQ));

    }

    @Test
    @Order(7)
    @DisplayName("Login Bad Request Test")
    public void loginBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () -> svc.login("{\"password\": \"password\"}"),
                "permissive to bad logins");
    }

    @Test
    @Order(8)
    @DisplayName("Test Logout")
    public void logoutTest(){
        svc.logout(existingAuth.authToken());
        Assertions.assertFalse(svc.tokenValid(existingAuth.authToken()), "authorization continues after logout");
    }

    @Test
    @Order(9)
    @DisplayName("Create Game No Errors")
    public void createGameBasicTest(){
        Assertions.assertDoesNotThrow(() -> {svc.createGame(NEW_GAME);}, "new game threw error");
    }

    @Test
    @Order(10)
    @DisplayName("Test listing a game")
    public void basicListGameTest(){
        try {
            int gameID = svc.createGame(NEW_GAME);
            var gameList = svc.listGames();
            Assertions.assertTrue(gameList.contains(
                    new GameData(gameID, null, null, "gameName", null)),
                    "could not find new game in gameList"
            );
            svc.clear();
            Assertions.assertTrue(svc.listGames().isEmpty(), "gameList not empty after clearing");
        } catch (DataAccessException ex){
            Assertions.fail("Unexpected Exception interrupted test: " + ex.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("New Game bad request")
    public void newGameBadRequestTest(){
        Assertions.assertThrows(
                DataAccessException.class, ()-> svc.createGame(""), "permissive to bad new game request"
        );
    }

    @Test
    @Order(12)
    @DisplayName("Authentication Testing")
    public void authenticationTest(){
        try {
            Assertions.assertTrue(svc.tokenValid(
                    existingAuth.authToken()), "Authentication failed for existing user"
            );
            svc.logout(existingAuth.authToken());
            Assertions.assertFalse(svc.tokenValid(existingAuth.authToken()), "Auth successful after logout");
            AuthData newAuth = new Gson().fromJson(svc.login(LOGIN_REQ), AuthData.class);
            Assertions.assertTrue(svc.tokenValid(newAuth.authToken()), "Authentication failed for new login");
        } catch (DataAccessException ex){
            Assertions.fail("Unexpected Error interrupted tests: " + ex.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("join chess game successful")
    public void joinGameSuccessBothColors(){
        try{
            svc.createGame(NEW_GAME);
            svc.joinGame(JOIN_GAME_WHITE, existingAuth.authToken());
            Assertions.assertTrue(svc.listGames().contains(new GameData(
                    1, "preUsername", null, "gameName", null)
            ), "joining white failed");
            svc.joinGame(JOIN_GAME_BLACK, existingAuth.authToken());
            Assertions.assertTrue(svc.listGames().contains(new GameData(
                    1, "preUsername", "preUsername", "gameName", null)
            ), "joining black failed");
        } catch (DataAccessException ex){
            Assertions.fail("Unexpected Error interrupted tests: " + ex.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Join Game color taken")
    public void joinGameFailureColorTaken(){
        try{
            svc.createGame(NEW_GAME);
            svc.joinGame(JOIN_GAME_WHITE, existingAuth.authToken());
            Assertions.assertThrows(
                    GameSpaceTaken.class, () -> svc.joinGame(JOIN_GAME_WHITE, existingAuth.authToken()),
                    "no error for white double joining"
            );
            svc.createGame(NEW_GAME);
            svc.joinGame("{\"playerColor\": \"BLACK\",\"gameID\": 2}", existingAuth.authToken());
            Assertions.assertThrows(
                    GameSpaceTaken.class,
                    () -> svc.joinGame("{\"playerColor\": \"BLACK\",\"gameID\": 2}", existingAuth.authToken()),
                    "no error for black double joining"
            );

        } catch (DataAccessException ex){
            Assertions.fail("Unexpected Error interrupted tests: " + ex.getMessage());
        }
    }

    @Test
    @Order(15)
    @DisplayName("Join Game Bad Request")
    public void joinGameBadRequest(){
        Assertions.assertDoesNotThrow(()->svc.createGame(NEW_GAME));
        Assertions.assertThrows(
                DataAccessException.class,
                ()->svc.joinGame("{\"playerColor\": \"WHITE\",\"gameID\": 2}",existingAuth.authToken()),
                "no error thrown for non-existent game"
        );
        Assertions.assertThrows(
                DataAccessException.class,
                ()->svc.joinGame("{\"playerColor\": \"WHI\",\"gameID\": 2}",existingAuth.authToken()),
                "no error thrown bad color"
        );
    }
}
