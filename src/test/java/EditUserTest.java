import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        // Регистрируем пользователя
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(anyOf(is(200), is(403))); // может быть 403 если уже существует

        // Логинимся и получаем accessToken
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(200);
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Обновить данные авторизованного пользователя")
    public void testUpdateUserWithAuth() {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"email\": \"newmail@yandex.ru\", \"name\": \"NewName\", \"password\": \"newpass123\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo("newmail@yandex.ru"))
                .body("user.name", equalTo("NewName"));
    }

    @Test
    @DisplayName("Обновить почту не авторизованного пользователя")
    public void testUpdateEmailWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"unauth@yandex.ru\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Обновить имя неавторизованного пользователя")
    public void testUpdateNameWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"NoAuthName\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Обновить пароль неавторизованного пользователя")
    public void testUpdatePasswordWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"password\": \"noauthpass\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @AfterAll
    public static void tearDown() {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .when()
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }
}
