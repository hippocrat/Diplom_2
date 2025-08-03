import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Логин под существующим пользователем, ожидаем ответ 200")
    public void loginWithUserExistsSuccess() {
        String requestBody = createLoginRequestBody("hilokea@yandex.ru", "kassian");
        sendLoginRequest(requestBody)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginWithWrongEmailAndPasswordReturnsError() {
        String requestBody = createLoginRequestBody("hilokeo@yandex.ru", "kasian");
        sendLoginRequest(requestBody)
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Создание тела запроса для логина: email = {email}, password = {password}")
    private String createLoginRequestBody(String email, String password) {
        return "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";
    }

    @Step("Отправка запроса на логин")
    private io.restassured.response.Response sendLoginRequest(String requestBody) {
        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/login");
    }
}
