package changeUser;

import api.requests.CreateUser;
import api.responses.CreateUserOk;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sender.MethodService;
import utils.TestDataGenerator;

import java.util.Locale;

import static constant.ResponseMessage.CHANGE_NOT_AUTH;
import static constant.Urls.CHANGE_USER;
import static constant.Urls.GET_USER;
import static org.junit.jupiter.api.Assertions.*;
import static utils.TestDataGenerator.generateEmail;

public class ChangeUserTest {
    static Faker faker = new Faker(new Locale("en-US"));
    private static final String courierLogin = faker.name().lastName();
    private static final String token = RandomStringUtils.randomAlphabetic(30);
    private static CreateUser createUser;
    private static String accessToken;

    @BeforeEach
    void createUser() {
        createUser = new TestDataGenerator().createUserGetAccessToken();
        accessToken = new TestDataGenerator().authUser(createUser);
        CreateUser createUserFirst = new TestDataGenerator().createUserGetAccessToken();
        createUserFirst.setName(createUser.getName());
        createUserFirst.setPassword(createUser.getPassword());
        createUserFirst.setEmail(createUser.getEmail());
    }

    @AfterEach
    void deleteUser() {
        new TestDataGenerator().deleteUser(new TestDataGenerator().authUser(createUser));
    }

    @Test
    @Description("Get user test")
    public void createCourierNotLineName() {
        Response getUser = MethodService.getRequests(GET_USER, accessToken);
        assertEquals(200, getUser.statusCode());
        assertTrue(getUser.as(CreateUserOk.class).getSuccess());
        assertEquals(createUser.getName(), getUser.as(CreateUserOk.class).getUser().getName());
        assertEquals(createUser.getEmail(), getUser.as(CreateUserOk.class).getUser().getEmail());
    }

    @Test
    @Description("Change name user test")
    public void changeUserName() {
        createUser.setName(courierLogin);
        Response changeUserName = MethodService.patchRequest(accessToken, CHANGE_USER, createUser);
        assertEquals(200, changeUserName.statusCode());
        assertTrue(changeUserName.as(CreateUserOk.class).getSuccess());

        Response getUser = MethodService.getRequests(GET_USER, accessToken);
        assertEquals(200, getUser.statusCode());
        assertTrue(getUser.as(CreateUserOk.class).getSuccess());
        assertEquals(createUser.getName(), getUser.as(CreateUserOk.class).getUser().getName());
        assertEquals(createUser.getEmail(), getUser.as(CreateUserOk.class).getUser().getEmail());
    }

    @Test
    @Description("Change email user test")
    public void changeUserEmail() {
        createUser.setEmail(generateEmail());
        Response changeUserEmail = MethodService.patchRequest(accessToken, CHANGE_USER, createUser);
        assertEquals(200, changeUserEmail.statusCode());
        assertTrue(changeUserEmail.as(CreateUserOk.class).getSuccess());

        Response getUser = MethodService.getRequests(GET_USER, accessToken);
        assertEquals(200, getUser.statusCode());
        assertTrue(getUser.as(CreateUserOk.class).getSuccess());
        assertEquals(createUser.getName(), getUser.as(CreateUserOk.class).getUser().getName());
        assertEquals(createUser.getEmail(), getUser.as(CreateUserOk.class).getUser().getEmail());
    }

    @Test
    @Description("Change user test not auth")// по спеке должен быть ответ 401. возвращает 403
    public void changeUserNotAuth() {
        createUser.setEmail(generateEmail());
        Response changeUserEmailNotAuth = MethodService.patchRequest(token, CHANGE_USER, createUser);
        assertEquals(401, changeUserEmailNotAuth.statusCode());
        assertFalse(changeUserEmailNotAuth.as(CreateUserOk.class).getSuccess());
        assertEquals(CHANGE_NOT_AUTH, changeUserEmailNotAuth.as(CreateUserOk.class).getMessage());
    }
}