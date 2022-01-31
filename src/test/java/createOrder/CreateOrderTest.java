package createOrder;

import api.requests.CreateUser;
import api.requests.Order;
import api.responses.OrderSuccess;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sender.MethodService;
import utils.GetIngredients;
import utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.Locale;

import static constant.ResponseMessage.NOT_INGREDIENTS;
import static constant.Urls.CREATE_ORDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateOrderTest {
    ArrayList notIngredients = new ArrayList();
    private static final String token = RandomStringUtils.randomAlphabetic(30);
    static Faker faker = new Faker(new Locale("en-US"));
    private static final String courierLogin = faker.name().lastName();
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
    @Description("Create order three ingredients")
    public void createOrder() {
        GetIngredients ingredients = new GetIngredients();
        Response createOrderUser = MethodService.postRequest(accessToken, CREATE_ORDER, ingredients.getRandomIngredients());
        assertEquals(200, createOrderUser.statusCode());
        System.out.println(createOrderUser.toString());
        assertTrue(createOrderUser.as(OrderSuccess.class).getSuccess());
        assertEquals(createUser.getName(), createOrderUser.as(OrderSuccess.class).getOrder().getOwner().getName());
    }

    @Test
    @Description("Create order not correct access token")
    public void createOrderNotAuth() {
        GetIngredients ingredients = new GetIngredients();
        Response createOrderUser = MethodService.postRequest(token, CREATE_ORDER, ingredients.getRandomIngredients());
        assertEquals(403, createOrderUser.statusCode());
    }

    @Test
    @Description("Create order not ingredients")
    public void createOrderNotIngredients() {
        Order order = Order.builder()
                .ingredients(notIngredients).build();

        Response createOrderUser = MethodService.postRequest(accessToken, CREATE_ORDER, order);
        assertEquals(400, createOrderUser.statusCode());
        assertEquals(NOT_INGREDIENTS, createOrderUser.as(OrderSuccess.class).getMessage());
    }

    @Test
    @Description("Create order null ingredients")
    public void createOrderNullIngredients() {
        notIngredients.add("");
        Order order = Order.builder()
                .ingredients(notIngredients).build();

        Response createOrderUser = MethodService.postRequest(accessToken, CREATE_ORDER, order);
        assertEquals(500, createOrderUser.statusCode());
    }

    @Test
    @Description("Create order not validate ingredients")
    public void createOrderNotValidateIngredients() {
        notIngredients.add("4566969878d");
        Order order = Order.builder()
                .ingredients(notIngredients).build();

        Response createOrderUser = MethodService.postRequest(accessToken, CREATE_ORDER, order);
        assertEquals(500, createOrderUser.statusCode());
    }
}