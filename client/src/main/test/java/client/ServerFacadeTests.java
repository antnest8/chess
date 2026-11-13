package client;

import model.AuthData;
import model.LoginAttempt;
import org.junit.jupiter.api.*;
import server.Server;
import model.UserData;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData normUser;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
        normUser = new UserData("newUser", "myPassword", "user@email.com");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    static void testSetup(){
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
            Assertions.fail("Unexpected Exception: Failed");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Register test: works")
    public void registerSuccessTest(){
        try {
            Assertions.assertInstanceOf(AuthData.class, facade.register(normUser));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: Failed");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Register test: double register failure")
    public void registerSuccessTest(){
        try {
            facade.register(normUser);
            Assertions.assertThrows(ResponseException.class, ()->facade.register(normUser));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: Failed");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Login test: failure")
    public void registerSuccessTest(){
        try {
            Assertions.assertThrows(ResponseException.class,
                    ()->facade.login(new LoginAttempt("notUser", "noPassword")));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: Failed");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Login test: success")
    public void registerSuccessTest(){
        try {
            facade.register(normUser);
            Assertions.assertInstanceOf(AuthData.class,
                    ()->facade.login(new LoginAttempt("newUser", "myPassword")));
        } catch(Exception e){
            Assertions.fail("Unexpected Exception: Failed");
        }
    }
}
