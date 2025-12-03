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
import static codes.nord.task.model.Action.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("NordCodes API")
@Feature("/endpoint")
public class EndpointTest extends BaseTest {
    private static final String VALID_TOKEN = "12866E1516504186B11ACB7DE2EA9F78";
    private static final String API_KEY = "qazWSXedc";

    @Test
    @DisplayName("Успешный флоу: LOGIN → ACTION → LOGOUT → повторный ACTION = ERROR")
    @Description("""
            Проверка полного жизненного цикла токена:
            1. LOGIN - успешная аутентификация
            2. ACTION - выполнение действия после LOGIN
            3. LOGOUT - завершение сессии
            4. ACTION после LOGOUT - должен вернуть ошибку
            """)
    public void test() {

        step_1();
        step_2();
        step_3();
        step_4();
    }

    @Step("Шаг 1. Выполняем LOGIN с валидным токеном")
    public void step_1() {
        Response resp = EndpointClient.sendPostRequest(LOGIN.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);
        assertAll(
                "Проверка успешного LOGIN",
                () -> assertThat("Должен вернуть статус " + HTTP_OK, resp.getStatusCode(), is(HTTP_OK)),
                () -> assertEquals("OK", body.result(), RESULT_SHOULD_BE_OK),
                () -> assertNull(body.message(), MESSAGE_SHOULD_BE_ABSENT)
        );

        attachJson("Ответ: LOGIN", resp.prettyPrint());
    }

    @Step("Шаг 2. Выполняем ACTION после успешного LOGIN")
    public void step_2() {
        Response resp = EndpointClient.sendPostRequest(ACTION.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка успешного ACTION",
                () -> assertThat("Должен вернуть статус " + HTTP_OK, resp.getStatusCode(), is(HTTP_OK)),
                () -> assertEquals("OK", body.result(), RESULT_SHOULD_BE_OK),
                () -> assertNull(body.message(), MESSAGE_SHOULD_BE_ABSENT)
        );
        attachJson("Ответ: ACTION", resp.prettyPrint());
    }

    @Step("Шаг 3. Выполняем LOGOUT")
    public void step_3() {
        Response resp = EndpointClient.sendPostRequest(LOGOUT.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка успешного LOGOUT",
                () -> assertThat("Должен вернуть статус " + HTTP_OK, resp.getStatusCode(), is(HTTP_OK)),
                () -> assertEquals("OK", body.result(), RESULT_SHOULD_BE_OK),
                () -> assertNull(body.message(), MESSAGE_SHOULD_BE_ABSENT)
        );
        attachJson("Ответ: LOGOUT", resp.prettyPrint());
    }

    @Step("Шаг 4. Проверяем, что после LOGOUT токен больше недействителен")
    public void step_4() {
        Response resp = EndpointClient.sendPostRequest(ACTION.name(), VALID_TOKEN, API_KEY);
        ResponseBody body = resp.as(ResponseBody.class);

        assertAll(
                "Проверка ACTION после LOGOUT",
                () -> assertThat("Должен вернуть статус " + HTTP_FORBIDDEN, resp.getStatusCode(), is(HTTP_FORBIDDEN)),
                () -> assertEquals("ERROR", body.result(), RESULT_SHOULD_BE_ERROR),
                () -> {
                    String message = body.message();
                    assertNotNull(message, MESSAGE_SHOULD_BE_PRESENT);
                    assertEquals("Token '" + VALID_TOKEN + "' not found", message, "Некорректное сообщение об ошибке");
                }
        );
        attachJson("Ответ: ACTION после LOGOUT", resp.prettyPrint());
    }
}
