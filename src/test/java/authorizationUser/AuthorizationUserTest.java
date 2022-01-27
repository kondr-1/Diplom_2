package authorizationUser;

import api.requests.CreateUser;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sender.MethodService;
import utils.TestDataGenerator;

import static constant.Urls.AUTHORIZATION_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorizationUserTest {
    private static CreateUser createUser;
    private static CreateUser createUserFirst;

    @BeforeEach
    void createUser() {
        createUser = new TestDataGenerator().createUserGetAccessToken();
        createUserFirst = new TestDataGenerator().createUserGetAccessToken();
        createUserFirst.setName(createUser.getName());
        createUserFirst.setPassword(createUser.getPassword());
    }

    @AfterEach
    void deleteUser() {
        new TestDataGenerator().deleteUser(new TestDataGenerator().authUser(createUser));
    }

    @Test
    @Description("Authorization user test")
    public void newCourier() {
        Response authorizationUser = MethodService.postRequest(AUTHORIZATION_USER, createUser);
        assertEquals(200, authorizationUser.statusCode());
    }

    @Test
    @Description("Auth not password test")
    public void createCourierNotLinePassword() {
        createUser.setPassword("Anton");
        Response authorizationUser = MethodService.postRequest(AUTHORIZATION_USER, createUser);
        assertEquals(401, authorizationUser.statusCode());
        createUser.setPassword(createUserFirst.getPassword());
    }

    @Test
    @Description("Auth not login test")//Согласно спеке должен упасть, но ответ 200
    public void createCourierNotLineName() {
        createUser.setName("Anton");
        Response authorizationUser = MethodService.postRequest(AUTHORIZATION_USER, createUser);
        assertEquals(401, authorizationUser.statusCode());
        createUser.setName(createUserFirst.getName());
    }
}