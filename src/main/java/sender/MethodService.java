package sender;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class MethodService {
    public static Response postRequest(String url, Object body) {
        Response response = (Response) given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post(url)
                .then()
                .extract();
        return response;
    }

    public static Response postRequest(String accessToken, String url, Object body) {
        Response response = (Response) given()
                .header("Content-type", "application/json")
                .and()
                .auth().oauth2(accessToken)
                .body(body)
                .when()
                .post(url)
                .then()
                .extract();
        return response;
    }

    public static Response patchRequest(String accessToken, String url, Object body) {
        Response response = (Response) given()
                .header("Content-type", "application/json")
                .and()
                .auth().oauth2(accessToken)
                .body(body)
                .when()
                .patch(url)
                .then()
                .extract();
        return response;
    }

    public static Response deleteRequests(String url, Object body, String idCourier) {
        Response response = (Response) given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .delete(String.format(url, idCourier))
                .then()
                .extract();
        return response;
    }

    public static Response deleteRequestsOauth2(String url, String accessToken) {
        Response response = (Response) given()
                 .and()
                .auth().oauth2(accessToken)
                .when()
                .delete(url)
                .then()
                .extract();
        return response;
    }

    public static Response getRequests(String url, String accessToken) {
        Response response = (Response) given()
                .and()
                .auth().oauth2(accessToken)
                .when()
                .get(url)
                .then()
                .extract();
        return response;
    }
    public static Response getRequests(String url) {
        Response response = (Response) given()
                .and()
                .when()
                .get(url)
                .then()
                .extract();
        return response;
    }
}