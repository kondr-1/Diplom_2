package createUser;

import api.requests.CreateUser;
import api.responses.CreateUserOk;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sender.MethodService;

import java.util.Locale;

import static constant.ResponseMessage.LOGIN_SOME_USER;
import static constant.ResponseMessage.NOT_LINE_USER;
import static constant.Urls.CREATE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static utils.TestDataGenerator.generateEmail;

public class CreateNewUserTest {
    private static CreateUser createUser;

    static Faker faker = new Faker(new Locale("en-US"));
    private static String courierLogin = faker.name().lastName();
    private static String courierPassword = faker.name().lastName() + faker.number().randomNumber();


    @Test
    @Description("Create new user test")
    public void newUser() {
        createUser = CreateUser.builder()
                .email(generateEmail())
                .password(courierPassword)
                .name(courierLogin)
                .build();

        Response createNewUser = MethodService.postRequest(CREATE_USER, createUser);
        assertEquals(200, createNewUser.statusCode());
        assertTrue(createNewUser.as(CreateUserOk.class).getSuccess());
        assertEquals(createUser.getName(), createNewUser.as(CreateUserOk.class).getUser().getName());
        assertEquals(createUser.getEmail(), createNewUser.as(CreateUserOk.class).getUser().getEmail());
        assertNotNull(createNewUser.as(CreateUserOk.class).getAccessToken());
        assertNotNull(createNewUser.as(CreateUserOk.class).getRefreshToken());
    }

    @Test
    @Description("Create two new user test")
    public void createTwoSameUser() {
        createUser = CreateUser.builder()
                .email(generateEmail())
                .password(courierPassword)
                .name(courierLogin)
                .build();

        Response createNewUserFirst = MethodService.postRequest(CREATE_USER, createUser);
        assertEquals(200, createNewUserFirst.statusCode());
        assertTrue(createNewUserFirst.as(CreateUserOk.class).getSuccess());
        Response createNewUserLast = MethodService.postRequest(CREATE_USER, createUser);
        assertEquals(403, createNewUserLast.statusCode());
        assertFalse(createNewUserLast.as(CreateUserOk.class).getSuccess());
        assertEquals(LOGIN_SOME_USER, createNewUserLast.as(CreateUserOk.class).getMessage());
    }

    public static Object[] getViewNotCreateCourier() {
        return new Object[][]{
                {generateEmail(), courierPassword, null},
                {null, courierPassword, courierLogin},
                {generateEmail(), null, courierLogin},
                {generateEmail(), "", courierLogin},
                {"", courierPassword, courierLogin},
                {generateEmail(), courierPassword, ""},
        };
    }

    @ParameterizedTest
    @Description("Create new user not line test")
    @MethodSource("getViewNotCreateCourier")
    public void createUserNotLine(String email, String password, String login) {
        createUser = CreateUser.builder()
                .email(email)
                .password(password)
                .name(login)
                .build();

        Response createNewUserNotLineOrNull = MethodService.postRequest(CREATE_USER, createUser);
        assertEquals(403, createNewUserNotLineOrNull.statusCode());
        assertEquals(NOT_LINE_USER, createNewUserNotLineOrNull.as(CreateUserOk.class).getMessage());
    }
}