package codes.nord.task.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class EndpointClient {

    @Step("Отправка POST запроса на /endpoint")
    public static Response sendPostRequest(String action, String token, String apiKey) {
        return given().header("X-Api-Key", apiKey)
                .contentType(ContentType.URLENC)
                .queryParam("token", token)
                .queryParam("action", action)
                .when()
                .log().ifValidationFails()
                .post("/endpoint")
                .then()
                .extract()
                .response();
    }
}
