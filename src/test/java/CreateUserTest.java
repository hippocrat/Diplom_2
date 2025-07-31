import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Создание нового уникального пользователя, ожидаем ответ 200")
    public void createNewUserSuccess() {
        String requestBody = "{\n" +
                "  \"email\": \"tteesstt-data@yandex.ru\",\n" +
                "  \"password\": \"kassian\",\n" +
                "  \"name\": \"WhiteWolf\"\n" +
                "}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание пользователя который уже есть в системе, ожидаем ответ 403")
    public void createUserAlreadyExistsReturnsError() {
        String requestBody = "{\n" +
                "  \"email\": \"tteesstt-data@yandex.ru\",\n" +
                "  \"password\": \"kassian\",\n" +
                "  \"name\": \"WhiteWolf\"\n" +
                "}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/register");
        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email, ожидаем ответ 403")
    public void createUserWithoutEmailReturnsError() {
        String requestBody = "{\n" +
                "  \"password\": \"kassian\",\n" +
                "  \"name\": \"WhiteWolf\"\n" +
                "}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    public void tearDown() {
        String requestBody = "{\n" +
                "  \"email\": \"tteesstt-data@yandex.ru\",\n" +
                "  \"password\": \"kassian\"\n" +
                "}";

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/auth/login");
        String token = response.jsonPath().getString("accessToken");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            given()
                    .auth().oauth2(token)
                    .delete("/api/auth/user");
        }
    }
}