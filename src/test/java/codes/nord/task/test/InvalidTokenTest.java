package codes.nord.task.test;

import codes.nord.task.client.EndpointClient;
import codes.nord.task.model.ResponseBody;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static codes.nord.task.config.ErrorMessages.MESSAGE_SHOULD_BE_PRESENT;
import static codes.nord.task.config.ErrorMessages.RESULT_SHOULD_BE_ERROR;
import static codes.nord.task.config.TestConfig.API_KEY;
import static codes.nord.task.model.Action.LOGIN;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("token")
public class InvalidTokenTest extends BaseTest {

    @ParameterizedTest(name = "LOGIN с невалидным токеном: {0}")
    @DisplayName("LOGIN с невалидным токеном (длина, спец. символы)")
    @ValueSource(strings = {
            "123", // слишком короткий
            "12866e1516504186b11acb7de2ea9f78", // маленькие буквы
            "12866E1516504186B11ACB7DE2EA9F788", // слишком длинный
            "12866E1516504186B11ACB7DE2EA9F7!", //спецсимвол
            ""// пустой
    })
    @Description("""
            Проверка валидации токена при LOGIN:
            - Токен должен содержать только A-F0-9
            - Токен должен быть длиной 32 символа
            - Невалидные токены должны возвращать ERROR
            """)
    @Step("Выполнение LOGIN запроса с невалидным токеном")
    public void loginWithInvalidTokenShouldReturnError(String invalidToken) {
        Response resp = EndpointClient.sendPostRequest(LOGIN.name(), invalidToken, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка ответа на невалидный токен",
                () -> assertThat("Должен вернуть статус " + HTTP_BAD_REQUEST, resp.getStatusCode(), is(HTTP_BAD_REQUEST)),
                () -> assertEquals("ERROR", body.result(), RESULT_SHOULD_BE_ERROR),
                () -> {
                    String message = body.message();
                    assertNotNull(message, MESSAGE_SHOULD_BE_PRESENT);
                    assertEquals("token: должно соответствовать \"^[0-9A-F]{32}$\"", message,
                            "Некорректное сообщение об ошибке");
                }
        );
        attachJson("Ответ для токена: " + invalidToken, resp.prettyPrint());
    }
}