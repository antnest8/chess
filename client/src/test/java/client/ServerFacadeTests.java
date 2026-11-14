package client;

import chess.ChessGame;
import client.exceptions.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;




public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData normUser;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        normUser = new UserData("newUser", "myPassword", "user@email.com");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void testSetup(){
        try {
            facade.clear();
        } catch (ResponseException e){
            throw new RuntimeException("Unexpected server exception during test setup. Exiting testing...");
        }
    }


    @Test
    @Order(1)
    @DisplayName("Clear test: no throw")
    public void clearExceptionTest() {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    @Order(2)
    @DisplayName("Clear test: cleared")
    public void clearLogicTest(){
        try {
            facade.register(normUser);
            facade.clear();
            Assertions.assertThrows(ResponseException.class,
                    () -> facade.login(new LoginAttempt("newUser", "myPassword")));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Register test: works")
    public void registerSuccessTest(){
        try {
            Assertions.assertInstanceOf(AuthData.class, facade.register(normUser));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Register test: double register failure")
    public void registerFailureTest(){
        try {
            facade.register(normUser);
            Assertions.assertThrows(ResponseException.class, ()->facade.register(normUser));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Login test: failure")
    public void loginFailureTest(){
        try {
            Assertions.assertThrows(ResponseException.class,
                    ()->facade.login(new LoginAttempt("notUser", "noPassword")));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Login test: success")
    public void loginSuccessTest(){
        try {
            facade.register(normUser);
            Assertions.assertInstanceOf(AuthData.class,
                    facade.login(new LoginAttempt("newUser", "myPassword")));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Logout test: success")
    public void logoutSuccessTest(){
        try {
            AuthData auth = facade.register(normUser);
            facade.logout(auth);
            Assertions.assertThrows(ResponseException.class,
                    ()->facade.listGames(auth));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Logout test: double try failure")
    public void logoutFailureTest(){
        try {
            AuthData auth = facade.register(normUser);
            facade.logout(auth);
            Assertions.assertThrows(ResponseException.class,
                    ()->facade.logout(auth));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("createGame test: success")
    public void createGameSuccessTest(){
        try {
            AuthData auth = facade.register(normUser);
            facade.createGame(auth, "myGame");
            Assertions.assertEquals(1, facade.listGames(auth).games().size());
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("createGame test: unauthorized")
    public void createGameFailUnauthTest(){
        try {
            AuthData auth = new AuthData("newUser", "100sad");
            Assertions.assertThrows(ResponseException.class, ()->facade.createGame(auth, "myGame"));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("listGame test: unauth")
    public void listGamesFailUnauth(){
        try {
            AuthData auth = new AuthData("newUser", "100sad");
            Assertions.assertThrows(ResponseException.class, ()->facade.listGames(auth));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("listGames test: success")
    public void listGamesSuccessTest(){
        try {
            AuthData auth = facade.register(normUser);
            Assertions.assertTrue(facade.listGames(auth).games().isEmpty());
            facade.createGame(auth, "myGame");
            Assertions.assertEquals(1, facade.listGames(auth).games().size());
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("joinGame test: success")
    public void joinGameTest(){
        try {
            AuthData auth = facade.register(normUser);
            facade.createGame(auth, "myGame");
            facade.joinGame(auth, 1, "WHITE");
            GameList newgames = facade.listGames(auth);
            Assertions.assertTrue(newgames.games().contains(
                    new GameData(1, "newUser", null, "myGame",
                            new SavedGame("WHITE", new ChessGame().getBoard().toString()))));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(15)
    @DisplayName("joinGame test fail: double use")
    public void joinGameTwiceFail(){
        try {
            AuthData auth = facade.register(normUser);
            facade.createGame(auth, "myGame");
            GameList games = facade.listGames(auth);
            facade.joinGame(auth, games.games().get(0).gameID(), "WHITE");
            Assertions.assertThrows(ResponseException.class, ()->facade.joinGame(auth, games.games().get(0).gameID(), "WHITE"));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(15)
    @DisplayName("joinGame test fail: no game")
    public void joinGameNoGameFail(){
        try {
            AuthData auth = facade.register(normUser);
            facade.createGame(auth, "myGame");
            Assertions.assertThrows(ResponseException.class, ()->facade.joinGame(auth, 3, "WHITE"));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    @DisplayName("joinGame test fail: unauth")
    public void joinGameUnauthFail(){
        try {
            AuthData auth = facade.register(normUser);
            facade.createGame(auth, "myGame");
            facade.logout(auth);
            Assertions.assertThrows(ResponseException.class, ()->facade.joinGame(auth, 1, "WHITE"));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: " + e.getMessage());
        }
    }


}
