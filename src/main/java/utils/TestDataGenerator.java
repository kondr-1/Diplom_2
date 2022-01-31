package utils;

import api.requests.CreateUser;
import api.responses.CreateUserOk;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import sender.MethodService;

import java.util.Locale;

import static constant.Urls.*;

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
        MethodService.postRequest(CREATE_USER, createUser);
        return createUser;
    }

    @Step("Auth user")
    public String authUser(CreateUser userAuth) {
        Response authorizationUser = MethodService.postRequest(AUTHORIZATION_USER, userAuth);
        return authorizationUser.as(CreateUserOk.class).getAccessToken().substring(7);
    }

    @Step("Delete user")
    public void deleteUser(String Bearer) {
        MethodService.deleteRequestsOauth2(DELETE_USER, Bearer);
    }

    @Step("Create order from user")
    public void createOrderUser(String accessToken) {
        GetIngredients ingredients = new GetIngredients();
        MethodService.postRequest(accessToken, CREATE_ORDER, ingredients.getRandomIngredients());
    }
}