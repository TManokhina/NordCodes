package codes.nord.task.test;

import codes.nord.task.client.EndpointClient;
import codes.nord.task.config.ErrorMessages;
import codes.nord.task.model.ResponseBody;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static codes.nord.task.config.ErrorMessages.MESSAGE_SHOULD_BE_PRESENT;
import static codes.nord.task.config.TestConfig.API_KEY;
import static codes.nord.task.config.TestConfig.VALID_TOKEN;
import static codes.nord.task.model.Action.ACTION;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("/endpoint")
public class ActionWithoutLoginTest extends BaseTest {

    @Test
    @DisplayName("ACTION без предварительного LOGIN → должен вернуть ERROR")
    @Description("""
            При отправке ACTION для токена, не прошедшего LOGIN:
            - возвращается HTTP 403
            - тело: { "result": "ERROR", "message": "<причина>" }
            """)
    public void actionWithoutLoginShouldReturnError() {
        step_1();
    }

    @Step("Шаг 1. Отправляем ACTION с валидным токеном, но без предварительного LOGIN")
    private void step_1() {
        Response resp = EndpointClient.sendPostRequest(ACTION.name(), VALID_TOKEN, API_KEY);
        var body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка тела ответа при ACTION без LOGIN",
                () -> assertThat("ACTION без предварительного LOGIN должен вернуть статус " + HTTP_FORBIDDEN,
                        resp.getStatusCode(), is(HTTP_FORBIDDEN)),
                () -> assertEquals("ERROR", body.result(), ErrorMessages.RESULT_SHOULD_BE_ERROR),
                () -> {
                    String message = body.message();
                    assertNotNull(message, MESSAGE_SHOULD_BE_PRESENT);
                    assertEquals("Token '" + VALID_TOKEN + "' not found", message, "Некорректное сообщение об ошибке");
                }
        );
        attachJson("Ответ: ACTION без LOGIN", resp.prettyPrint());
    }
}