import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

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
        String requestBody = "{\n" +
                "  \"email\": \"hilokea@yandex.ru\",\n" +
                "  \"password\": \"kassian\"\n" +
                "}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginWithWrongEmailAndPasswordReturnsError() {
        String requestBody = "{\n" +
                "  \"email\": \"hilokeo@yandex.ru\",\n" +
                "  \"password\": \"kasian\"\n" +
                "}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message",equalTo("email or password are incorrect"));
    }
}
