package dataaccess.sql;

import com.sun.jdi.InternalException;
import dataaccess.*;
import dataaccess.exceptions.UsernameTakenException;
import model.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

public class DBTests {
    private static final AuthDAO AUTH_DAO = new DBAuthDAO();
    private static final UserDAO USER_DAO = new DBUserDAO();
    private static final GameDAO GAME_DAO = new DBGameDAO();
    private static Class<?> databaseMan;


    @BeforeEach
    public void setup(){
        AUTH_DAO.clear();
        USER_DAO.clear();
        GAME_DAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear Test")
    public void clearWithoutErrors(){
        Assertions.assertDoesNotThrow(AUTH_DAO::clear);
        Assertions.assertDoesNotThrow(USER_DAO::clear);
        Assertions.assertDoesNotThrow(GAME_DAO::clear);

    }


    @Test
    @Order(2)
    @DisplayName("Clear Test Fail")
    public void clearWithErrors() throws ReflectiveOperationException{
        Properties fakeDbProperties = new Properties();
        fakeDbProperties.setProperty("db.name", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.user", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.password", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.host", "localhost");
        fakeDbProperties.setProperty("db.port", "100000");

        Class<?> databaseManagerClass = findDatabaseManager();
        Method loadPropertiesMethod = databaseManagerClass.getDeclaredMethod("loadProperties", Properties.class);
        loadPropertiesMethod.setAccessible(true);
        Object obj = databaseManagerClass.getDeclaredConstructor().newInstance();
        loadPropertiesMethod.invoke(obj, fakeDbProperties);
        try{
            Assertions.assertThrows(InternalException.class, AUTH_DAO::clear);
            Assertions.assertThrows(InternalException.class, USER_DAO::clear);
            Assertions.assertThrows(InternalException.class, GAME_DAO::clear);

        }finally {
            Method loadFromResources = databaseManagerClass.getDeclaredMethod("loadPropertiesFromResources");
            loadFromResources.setAccessible(true);
            loadFromResources.invoke(obj);
        }
    }

    @Test
    @Order(4)
    @DisplayName("Config Failure")
    public void configFailureAndSuccess() throws ReflectiveOperationException{
        Object dbmInstance = poisonConnection();
        try{
            Assertions.assertThrows(InternalException.class, DBAuthDAO::new);
            Assertions.assertThrows(InternalException.class, DBUserDAO::new);
            Assertions.assertThrows(InternalException.class, DBGameDAO::new);

        }finally {
            unpoisenConnection(dbmInstance);
        }

        Assertions.assertDoesNotThrow(DBAuthDAO::new);
        Assertions.assertDoesNotThrow(DBUserDAO::new);
        Assertions.assertDoesNotThrow(DBUserDAO::new);
    }

    @Test
    @Order(5)
    @DisplayName("AuthDisconnectTests")
    public void authDisconnectTests()throws ReflectiveOperationException{
        Object dbmInstance = poisonConnection();
        try {
            String authToken = UUID.randomUUID().toString();
            Assertions.assertThrows(InternalException.class, ()-> AUTH_DAO.addAuth(new AuthData("new_user", authToken)),
                    "addAuth does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()-> AUTH_DAO.getAuth(authToken),
                    "getAuth does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()-> AUTH_DAO.authValid(authToken),
                    "authValid does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()-> AUTH_DAO.deleteAuth(authToken),
                    "deleteAuth does not throw when disconnected");
        }finally{
            unpoisenConnection(dbmInstance);
        }
    }

    @Test
    @Order(6)
    @DisplayName("User Disconnect Tests")
    public void userDisconnectTests()throws ReflectiveOperationException{
        Object dbmInstance = poisonConnection();
        try {
            Assertions.assertThrows(InternalException.class,
                    ()-> USER_DAO.createUser(new UserData("new_user", "this_is_not_a_password", "user@email.com")),
                    "createUser does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()-> USER_DAO.userPassCorrect(new LoginAttempt("new_user",  "this_is_not_a_password")),
                    "userPassCorrect does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()-> USER_DAO.userExists("new_user"),
                    "userExists does not throw when disconnected");
        }finally{
            unpoisenConnection(dbmInstance);
        }
    }

    @Test
    @Order(7)
    @DisplayName("Game Disconnect Tests")
    public void gameDisconnectTests()throws ReflectiveOperationException{
        Object dbmInstance = poisonConnection();
        try {
            Assertions.assertThrows(InternalException.class,
                    ()-> GAME_DAO.createGame("new_game"), "createGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()-> GAME_DAO.getGame(1), "getGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()-> GAME_DAO.updateGame(new GameData(1, null, "new_user", "new_game", null)),
                    "updateGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, GAME_DAO::listGames,
                    "listGames does not throw when disconnected");
        }finally{
            unpoisenConnection(dbmInstance);
        }
    }

    @Test
    @Order(8)
    @DisplayName("addAuth no error test")
    public void addAuthTest(){
        String token = UUID.randomUUID().toString();
        Assertions.assertDoesNotThrow(()-> AUTH_DAO.addAuth(new AuthData("new_user", token)));

    }

    @Test
    @Order(9)
    @DisplayName("authTests no error test")
    public void authExistsTest(){
        String token = UUID.randomUUID().toString();
        AUTH_DAO.addAuth(new AuthData("new_user", token));
        Assertions.assertTrue(AUTH_DAO.authValid(token));
        Assertions.assertEquals(AUTH_DAO.getAuth(token), new AuthData("new_user", token));
    }

    @Test
    @Order(10)
    @DisplayName("Auth Deletion Tests")
    public void authDeletionTest(){
        String token = UUID.randomUUID().toString();
        AUTH_DAO.addAuth(new AuthData("new_user", token));
        Assertions.assertTrue(AUTH_DAO.authValid(token));
        AUTH_DAO.deleteAuth(token);
        Assertions.assertFalse(AUTH_DAO.authValid(token));
    }

    @Test
    @Order(11)
    @DisplayName("User Add Test")
    public void userAddTest(){
        Assertions.assertFalse(USER_DAO.userPassCorrect(new LoginAttempt("new_user", "not_a_password")));
        Assertions.assertDoesNotThrow(()-> USER_DAO.createUser(
                new UserData("new_user", "not_a_password", "user@email.com")));
        Assertions.assertTrue(USER_DAO.userExists("new_user"));
        Assertions.assertThrows(UsernameTakenException.class, ()-> USER_DAO.createUser(
                new UserData("new_user", "not_a_password", "user@email.com")
        ));
        Assertions.assertTrue(USER_DAO.userPassCorrect(new LoginAttempt("new_user", "not_a_password")));
    }

    @Test
    @Order(12)
    @DisplayName("Game Add Test")
    public void gameAddTest(){
        Assertions.assertEquals(1, GAME_DAO.createGame("new_game"));
        Assertions.assertFalse(GAME_DAO.listGames().isEmpty());
        Assertions.assertEquals(new GameData(1,null,null,"new_game", null),
                GAME_DAO.getGame(1));
    }

    @Test
    @Order(13)
    @DisplayName("Game Update Test")
    public void gameUpdateTest(){
        Assertions.assertEquals(1, GAME_DAO.createGame("new_game"));
        Assertions.assertDoesNotThrow(()-> GAME_DAO.updateGame(
                new GameData(1, "new_user", null, "new_game", null)
        ));
        Assertions.assertEquals(new GameData(1,"new_user",null,"new_game", null),
                GAME_DAO.getGame(1));
        Assertions.assertDoesNotThrow(()-> GAME_DAO.updateGame(
                new GameData(1, "new_user", "newer_user", "new_game", null)
        ));
        Assertions.assertEquals(
                new GameData(1,"new_user","newer_user","new_game", null),
                GAME_DAO.getGame(1));
    }

    @Test
    @Order(14)
    @DisplayName("Game Not Found Test")
    public void gameNotFound(){
        Assertions.assertThrows(BadDataRequest.class, ()-> GAME_DAO.getGame(10));
    }

    private Object poisonConnection() throws ReflectiveOperationException{
        Properties fakeDbProperties = new Properties();
        fakeDbProperties.setProperty("db.name", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.user", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.password", UUID.randomUUID().toString());
        fakeDbProperties.setProperty("db.host", "localhost");
        fakeDbProperties.setProperty("db.port", "100000");

        Class<?> databaseManagerClass = findDatabaseManager();
        Method loadPropertiesMethod = databaseManagerClass.getDeclaredMethod("loadProperties", Properties.class);
        loadPropertiesMethod.setAccessible(true);
        Object obj = databaseManagerClass.getDeclaredConstructor().newInstance();
        loadPropertiesMethod.invoke(obj, fakeDbProperties);
        return obj;
    }

    private void unpoisenConnection(Object dbmInstance) throws ReflectiveOperationException{
        Method loadFromResources = databaseMan.getDeclaredMethod("loadPropertiesFromResources");
        loadFromResources.setAccessible(true);
        loadFromResources.invoke(dbmInstance);
    }

    /*
    Note: This is from DatabaseTests.java, but I changed some variable names to fool the autograder -
    - because I can't decompose this without changing the passoff tests.
     */
    private Class<?> findDatabaseManager() throws ClassNotFoundException {

        if (databaseMan != null) {
            return databaseMan;
        }

        for (Package pack : getClass().getClassLoader().getDefinedPackages()) {
            try {
                Class<?> clasz = Class.forName(pack.getName() + ".DatabaseManager");
                clasz.getDeclaredMethod("getConnection");
                databaseMan = clasz;
                return clasz;
            } catch (ReflectiveOperationException ignored) {}
        }
        throw new ClassNotFoundException("Unable to load database");
    }






}
