import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import requests.SignUpRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class EditUserTest {

    private static String baseUri = "https://stellarburgers.nomoreparties.site";
    private static String accessToken;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = baseUri;

        String email = "lecroissant@yandex.ru";
        String password = "adb123";
        String name = "leCroissant";

        registerUser(email, password, name);
        accessToken = loginAndGetToken(email, password);
    }

    @Step("Регистрация пользователя с email: {0}, name: {2}")
    public static void registerUser(String email, String password, String name) {
        SignUpRequest signUpRequest = new SignUpRequest(email, password, name);
        given()
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(anyOf(is(200), is(403))); // 403 если уже зарегистрирован
    }

    @Step("Логин пользователя с email: {0}")
    public static String loginAndGetToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email,password);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(200);
        return loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Обновить данные авторизованного пользователя")
    public void testUpdateUserWithAuth() {
        updateUser("newmail@yandex.ru", "NewName", "newpass123", accessToken);
    }

    @Step("Обновление пользователя с токеном авторизации")
    public void updateUser(String email, String name, String password, String token) {
        SignUpRequest signUpRequest = new SignUpRequest(email, name, password);
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Обновить почту не авторизованного пользователя")
    public void testUpdateEmailWithoutAuth() {
        updateUserWithoutAuth("{\"email\": \"unauth@yandex.ru\"}");
    }

    @Test
    @DisplayName("Обновить имя неавторизованного пользователя")
    public void testUpdateNameWithoutAuth() {
        updateUserWithoutAuth("{\"name\": \"NoAuthName\"}");
    }

    @Test
    @DisplayName("Обновить пароль неавторизованного пользователя")
    public void testUpdatePasswordWithoutAuth() {
        updateUserWithoutAuth("{\"password\": \"noauthpass\"}");
    }

    @Step("Попытка обновления пользователя без авторизации с телом запроса: {0}")
    public void updateUserWithoutAuth(String requestBody) {
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @AfterAll
    public static void tearDown() {
        deleteUser(accessToken);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String token) {
        if (token != null) {
            given()
                    .header("Authorization", token)
                    .when()
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }
}
