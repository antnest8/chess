package client;

import model.LoginAttempt;
import org.junit.jupiter.api.*;
import server.Server;
import model.UserData;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
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
        facade.register(new UserData("newUser", "myPassword", "user@email.com"));
        Assertions.assertDoesNotThrow(()->facade.clear());
        Assertions.assertThrows(ResponseException.class,
                ()->facade.login(new LoginAttempt("newUser", "myPassword")));

    }
}
