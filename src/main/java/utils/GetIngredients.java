package utils;

import api.requests.Order;
import api.responses.Data;
import api.responses.Ingredients;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import sender.MethodService;

import java.util.ArrayList;
import java.util.List;

import static constant.Urls.GET_INGREDIENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetIngredients {

    ArrayList sauce = new ArrayList();
    ArrayList main = new ArrayList();
    ArrayList bun = new ArrayList();
    ArrayList ingredients = new ArrayList();

    @Step("Create User")
    public Order getRandomIngredients() {
        Response getIngredients = MethodService.getRequests(GET_INGREDIENTS);
        assertEquals(200, getIngredients.statusCode());
        List<Data> data = getIngredients.as(Ingredients.class).getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getType().equals("bun")) {
                bun.add(data.get(i).get_id());
            } else if (data.get(i).getType().equals("main")) {
                main.add(data.get(i).get_id());
            } else if (data.get(i).getType().equals("sauce")) {
                sauce.add(data.get(i).get_id());
            }
        }
        ingredients.add(main.get((int) (Math.random() * main.size())));
        ingredients.add(bun.get((int) (Math.random() * bun.size())));
        ingredients.add(sauce.get((int) (Math.random() * sauce.size())));

        return Order.builder()
                .ingredients(ingredients).build();
    }
}