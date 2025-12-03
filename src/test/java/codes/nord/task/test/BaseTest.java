package codes.nord.task.test;

import codes.nord.task.client.EndpointClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static codes.nord.task.config.TestConfig.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class BaseTest {

    protected static WireMockServer wireMockServer;
    private static Process appProcess;

    @BeforeAll
    static void globalSetup() {
        startWireMock();
        RestAssured.baseURI = BASE_URL;
    }

    @AfterAll
    static void globalTeardown() {
        EndpointClient.sendPostRequest("LOGOUT", VALID_TOKEN, API_KEY);

        if (appProcess != null && appProcess.isAlive()) {
            appProcess.destroyForcibly();
        }
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private static void startWireMock() {
        wireMockServer = new WireMockServer(MOCK_PORT);
        wireMockServer.start();

        //по умолчанию — успешные ответы внешнего сервиса
        wireMockServer.stubFor(post("/auth")
                .willReturn(aResponse().withStatus(200)));

        wireMockServer.stubFor(post("/doAction")
                .willReturn(aResponse().withStatus(200)));
    }

    //утилиты для тестов
    protected void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json, ".json");
    }
}