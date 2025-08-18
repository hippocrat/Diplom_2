package tests;

import configs.BaseTest;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Логин под существующим пользователем, ожидаем ответ 200")
    public void loginWithUserExistsSuccess() {
        LoginRequest requestBody = createLoginRequestBody("hilokea@yandex.ru", "kassian");
        sendLoginRequest(requestBody)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginWithWrongEmailAndPasswordReturnsError() {
        LoginRequest requestBody = createLoginRequestBody("hilokeo@yandex.ru", "kasian");
        sendLoginRequest(requestBody)
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Создание тела запроса для логина: email = {email}, password = {password}")
    private LoginRequest createLoginRequestBody(String email, String password) {
        return new LoginRequest(email,password);
    }

    @Step("Отправка запроса на логин")
    private io.restassured.response.Response sendLoginRequest(LoginRequest requestBody) {
        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/login");
    }
}
