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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static codes.nord.task.config.ErrorMessages.*;
import static codes.nord.task.model.Action.LOGIN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("apiKey")
public class InvalidApiKeyTest extends BaseTest {

    @ParameterizedTest(name = "Неверный API ключ: {0}")
    @DisplayName("Проверка неверных API ключей")
    @ValueSource(strings = {
            "",
            "wrongApiKey",
    })
    @Description("Запрос с неверным API ключом должен возвращать ошибку")
    @Step("Отправка запроса с неверным API ключом: [{apiKey}]")
    public void requestWithWrongApiKeyShouldReturnError(String apiKey) {
        Response resp = EndpointClient.sendPostRequest(LOGIN.name(), TestConfig.VALID_TOKEN, apiKey);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка ответа с неверным API ключом",
                () -> assertThat("Должен вернуть статус " + HTTP_UNAUTHORIZED,
                        resp.getStatusCode(), is(HTTP_UNAUTHORIZED)),
                () -> assertEquals("ERROR", body.result(), RESULT_SHOULD_BE_ERROR),
                () -> assertNotNull(body.message(), MESSAGE_SHOULD_BE_PRESENT),
                () -> {
                    String message = body.message();
                    assertNotNull(message, MESSAGE_SHOULD_BE_PRESENT);
                    assertEquals("Missing or invalid API Key", message,
                            "Некорректное сообщение об ошибке");
                }
        );
        attachJson("Ответ для API ключа: " + apiKey, resp.prettyPrint());
    }
}