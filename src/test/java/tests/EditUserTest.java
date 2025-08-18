package tests;

import configs.BaseTest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import requests.SignUpRequest;
import steps.AuthSteps;
import steps.CreateUserSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class EditUserTest extends BaseTest {

    private static String accessToken;
    private static CreateUserSteps createUserSteps;
    private static AuthSteps authSteps;

    @BeforeAll
    public static void setUp() {
        String email = "lecroissant@yandex.ru";
        String password = "adb123";
        String name = "leCroissant";

        createUserSteps.registerUser(email, password, name);
        accessToken = authSteps.loginAndGetAccessToken(email, password);
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

    @AfterAll
    public static void tearDown() {
        createUserSteps.deleteUser(accessToken);
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
}
