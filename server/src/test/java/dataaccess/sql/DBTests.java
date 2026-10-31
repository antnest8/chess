package dataaccess.sql;

import com.sun.jdi.InternalException;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UsernameTakenException;
import model.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DBTests {
    private static final AuthDAO authDAO = new DBAuthDAO();
    private static final UserDAO userDAO = new DBUserDAO();
    private static final GameDAO gameDAO = new DBGameDAO();
    private static Class<?> databaseManagerClass;


    @BeforeEach
    public void setup(){
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear Test")
    public void clearWithoutErrors(){
        Assertions.assertDoesNotThrow(authDAO::clear);
        Assertions.assertDoesNotThrow(userDAO::clear);
        Assertions.assertDoesNotThrow(gameDAO::clear);

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
            Assertions.assertThrows(InternalException.class, authDAO::clear);
            Assertions.assertThrows(InternalException.class, userDAO::clear);
            Assertions.assertThrows(InternalException.class, gameDAO::clear);

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
            Assertions.assertThrows(InternalException.class, ()->authDAO.addAuth(new AuthData("new_user", authToken)),
                    "addAuth does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()->authDAO.getAuth(authToken),
                    "getAuth does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()->authDAO.authValid(authToken),
                    "authValid does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()->authDAO.deleteAuth(authToken),
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
                    ()->userDAO.createUser(new UserData("new_user", "this_is_not_a_password", "user@email.com")),
                    "createUser does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()->userDAO.userPassCorrect(new LoginAttempt("new_user",  "this_is_not_a_password")),
                    "userPassCorrect does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, ()->userDAO.userExists("new_user"),
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
                    ()->gameDAO.createGame("new_game"), "createGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()->gameDAO.getGame(1), "getGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class,
                    ()->gameDAO.updateGame(new GameData(1, null, "new_user", "new_game", null)),
                    "updateGame does not throw when disconnected");
            Assertions.assertThrows(InternalException.class, gameDAO::listGames,
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
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(new AuthData("new_user", token)));

    }

    @Test
    @Order(9)
    @DisplayName("authTests no error test")
    public void authExistsTest(){
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData("new_user", token));
        Assertions.assertTrue(authDAO.authValid(token));
        Assertions.assertEquals(authDAO.getAuth(token), new AuthData("new_user", token));
    }

    @Test
    @Order(10)
    @DisplayName("Auth Deletion Tests")
    public void authDeletionTest(){
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData("new_user", token));
        Assertions.assertTrue(authDAO.authValid(token));
        authDAO.deleteAuth(token);
        Assertions.assertFalse(authDAO.authValid(token));
    }

    @Test
    @Order(11)
    @DisplayName("User Add Test")
    public void userAddTest(){
        Assertions.assertFalse(userDAO.userPassCorrect(new LoginAttempt("new_user", "not_a_password")));
        Assertions.assertDoesNotThrow(()->userDAO.createUser(
                new UserData("new_user", "not_a_password", "user@email.com")));
        Assertions.assertTrue(userDAO.userExists("new_user"));
        Assertions.assertThrows(UsernameTakenException.class, ()->userDAO.createUser(
                new UserData("new_user", "not_a_password", "user@email.com")
        ));
        Assertions.assertTrue(userDAO.userPassCorrect(new LoginAttempt("new_user", "not_a_password")));
    }

    @Test
    @Order(12)
    @DisplayName("Game Add Test")
    public void gameAddTest(){
        Assertions.assertEquals(1, gameDAO.createGame("new_game"));
        Assertions.assertFalse(gameDAO.listGames().isEmpty());
        Assertions.assertEquals(new GameData(1,null,null,"new_game", null),
                gameDAO.getGame(1));
    }

    @Test
    @Order(13)
    @DisplayName("Game Update Test")
    public void gameUpdateTest(){
        Assertions.assertEquals(1, gameDAO.createGame("new_game"));
        Assertions.assertDoesNotThrow(()->gameDAO.updateGame(
                new GameData(1, "new_user", null, "new_game", null)
        ));
        Assertions.assertEquals(new GameData(1,"new_user",null,"new_game", null),
                gameDAO.getGame(1));
        Assertions.assertDoesNotThrow(()->gameDAO.updateGame(
                new GameData(1, "new_user", "newer_user", "new_game", null)
        ));
        Assertions.assertEquals(
                new GameData(1,"new_user","newer_user","new_game", null),
                gameDAO.getGame(1));
    }

    @Test
    @Order(14)
    @DisplayName("Game Not Found Test")
    public void gameNotFound(){
        Assertions.assertThrows(BadDataRequest.class, ()->gameDAO.getGame(10));
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
        Method loadFromResources = databaseManagerClass.getDeclaredMethod("loadPropertiesFromResources");
        loadFromResources.setAccessible(true);
        loadFromResources.invoke(dbmInstance);
    }

    private Class<?> findDatabaseManager() throws ClassNotFoundException {
        if(databaseManagerClass != null) {
            return databaseManagerClass;
        }

        for (Package p : getClass().getClassLoader().getDefinedPackages()) {
            try {
                Class<?> clazz = Class.forName(p.getName() + ".DatabaseManager");
                clazz.getDeclaredMethod("getConnection");
                databaseManagerClass = clazz;
                return clazz;
            } catch (ReflectiveOperationException ignored) {}
        }
        throw new ClassNotFoundException("Unable to load database in order to verify persistence. " +
                "Are you using DatabaseManager to set your credentials? " +
                "Did you edit the signature of the getConnection method?");
    }






}
