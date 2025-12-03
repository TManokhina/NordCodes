package codes.nord.task.test;

import codes.nord.task.client.EndpointClient;
import codes.nord.task.model.ResponseBody;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static codes.nord.task.config.ErrorMessages.*;
import static codes.nord.task.config.TestConfig.API_KEY;
import static codes.nord.task.config.TestConfig.VALID_TOKEN;
import static codes.nord.task.model.Action.LOGIN;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("login")
public class DoubleLoginTest extends BaseTest {

    @Test
    @DisplayName("Повторный LOGIN того же токена")
    @Description("""
            Проверка поведения при повторном LOGIN с одним и тем же токеном:
            1. Первый LOGIN должен быть успешным
            2. Второй LOGIN того же токена должен:
            - возвращать HTTP 409
            - тело: { "result": "ERROR", "message": "<причина>" }
            """)
    public void test() {
        step_1();
        step_2();
    }

    @Step("Шаг 1. Выполняем LOGIN с валидным токеном")
    private void step_1() {
        Response resp = EndpointClient.sendPostRequest(LOGIN.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка первого LOGIN",
                () -> assertThat("Должен вернуть статус " + HTTP_OK, resp.getStatusCode(), is(HTTP_OK)),
                () -> assertEquals("OK", body.result(), "result должен быть OK"),
                () -> assertNull(body.message(), MESSAGE_SHOULD_BE_ABSENT)
        );
    }

    @Step("Шаг 2. Второй LOGIN (повторный - с тем же токеном)")
    private void step_2() {
        Response resp = EndpointClient.sendPostRequest(LOGIN.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка повторного LOGIN",
                () -> assertThat("Должен вернуть статус " + HTTP_CONFLICT, resp.getStatusCode(), is(HTTP_CONFLICT)),
                () -> assertEquals("ERROR", body.result(), RESULT_SHOULD_BE_ERROR),
                () -> {
                    String message = body.message();
                    assertNotNull(message, MESSAGE_SHOULD_BE_PRESENT);
                    assertEquals("Token '" + VALID_TOKEN + "' already exists", message, "Неверное сообщение об ошибке");
                }
        );
        attachJson("Ответ: повторный LOGIN", resp.prettyPrint());
    }
}