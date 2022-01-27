package utils;

import api.requests.CreateUser;
import api.responses.CreateUserOk;
import api.responses.OrderSuccess;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import sender.MethodService;

import java.util.Locale;

import static constant.Urls.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDataGenerator {
    private static CreateUser createUser;

    static Faker faker = new Faker(new Locale("en-US"));
    private static String courierLogin = faker.name().lastName();
    private static String courierPassword = faker.name().lastName() + faker.number().randomNumber();


    private static final Faker generator = Faker.instance(new Locale("ru_RU"));
    private static final Faker generatorEn = Faker.instance(new Locale("en_EN"));

    public static String generateEmail() {
        return generatorEn.name().firstName().toLowerCase(Locale.ROOT) + generator.numerify("#####") + "@ant.mail.ru";
    }

    @Step("Create User")
    public CreateUser createUserGetAccessToken() {
        createUser = CreateUser.builder()
                .email(generateEmail())
                .password(courierPassword)
                .name(courierLogin)
                .build();
        Response createNewUser = MethodService.postRequest(CREATE_USER, createUser);
        assertEquals(200, createNewUser.statusCode());
        return createUser;
    }

    @Step("Auth user")
    public String authUser(CreateUser userAuth) {
        Response authorizationUser = MethodService.postRequest(AUTHORIZATION_USER, userAuth);
        assertEquals(200, authorizationUser.statusCode());
        return authorizationUser.as(CreateUserOk.class).getAccessToken().substring(7);
    }

    @Step("Delete user")
    public void deleteUser(String Bearer) {
        Response deleteUser = MethodService.deleteRequestsOauth2(DELETE_USER, Bearer);
        assertEquals(202, deleteUser.statusCode());
    }

    @Step("Create order from user")
    public void createOrderUser(String accessToken) {
        GetIngredients ingredients = new GetIngredients();
        Response createOrderUser = MethodService.postRequest(accessToken, CREATE_ORDER, ingredients.getRandomIngredients());
        assertEquals(200, createOrderUser.statusCode());
        assertTrue(createOrderUser.as(OrderSuccess.class).getSuccess());
    }
}