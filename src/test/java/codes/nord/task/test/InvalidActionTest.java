package codes.nord.task.test;

import codes.nord.task.client.EndpointClient;
import codes.nord.task.config.TestConfig;
import codes.nord.task.model.ResponseBody;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static codes.nord.task.config.ErrorMessages.RESULT_SHOULD_BE_ERROR;
import static codes.nord.task.config.TestConfig.API_KEY;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("action")
public class InvalidActionTest extends BaseTest {

    @Test
    @DisplayName("Неверное значение action")
    @Description("Запрос с несуществующим action должен возвращать ERROR")
    public void test() {
        step_1();
    }

    @Step("Выполняем запрос с несуществующим action")
    private void step_1() {
        Response resp = EndpointClient.sendPostRequest("INVALID_ACTION", TestConfig.VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка ответа на неверное действие",
                () -> assertThat("Должен вернуть статус " + HTTP_BAD_REQUEST, resp.getStatusCode(),
                        is(HTTP_BAD_REQUEST)),
                () -> assertEquals("ERROR", body.result(), RESULT_SHOULD_BE_ERROR),
                () -> {
                    String message = body.message();
                    assertNotNull(message, "message должен присутствовать");
                    assertEquals("action: invalid action 'INVALID_ACTION'. Allowed: LOGIN, LOGOUT, ACTION", message,
                            "Некорректное сообщение об ошибке");
                }
        );
        attachJson("Ответ: неверное действие", resp.prettyPrint());
    }
}
