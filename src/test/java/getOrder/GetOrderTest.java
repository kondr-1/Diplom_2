package getOrder;

import api.requests.CreateUser;
import api.responses.GetOrders;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sender.MethodService;
import utils.TestDataGenerator;

import static constant.ResponseMessage.CHANGE_NOT_AUTH;
import static constant.Urls.GET_ORDER;
import static org.junit.jupiter.api.Assertions.*;

public class GetOrderTest {
    private static CreateUser createUser;
    private static String accessToken;

    @BeforeEach
    void createUser() {
        createUser = new TestDataGenerator().createUserGetAccessToken();
        accessToken = new TestDataGenerator().authUser(createUser);
        new TestDataGenerator().createOrderUser(accessToken);
    }

    @AfterEach
    void deleteUser() {
        new TestDataGenerator().deleteUser(new TestDataGenerator().authUser(createUser));
    }

    @Test
    @Description("Get orders user")
    public void getOrdersUser() {
        Response orderUser = MethodService.getRequests(GET_ORDER, accessToken);
        assertEquals(200, orderUser.statusCode());
        assertTrue(orderUser.as(GetOrders.class).getSuccess());
        assertNotNull(orderUser.as(GetOrders.class).getOrders().get(0));
    }

    @Test
    @Description("Get orders user not auth")
    public void getOrdersUserNotAuth() {
        Response orderUser = MethodService.getRequests(GET_ORDER);
        assertEquals(401, orderUser.statusCode());
        assertEquals(CHANGE_NOT_AUTH, orderUser.as(GetOrders.class).getMessage());
    }
}