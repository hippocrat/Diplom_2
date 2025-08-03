import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    private static String baseUri = "https://stellarburgers.nomoreparties.site";
    String email = "tteesstt-data@yandex.ru";
    String password = "kassian";
    String name = "WhiteWolf";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = baseUri;
    }

    @Test
    @DisplayName("Создание нового уникального пользователя, ожидаем ответ 200")
    public void createNewUserSuccess() {
        registerUser(email, password, name)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание пользователя который уже есть в системе, ожидаем ответ 403")
    public void createUserAlreadyExistsReturnsError() {
        registerUser(email, password, name);
        registerUser(email, password, name)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email, ожидаем ответ 403")
    public void createUserWithoutEmailReturnsError() {
        String requestBody = "{\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"name\": \"" + name + "\"\n" +
                "}";

        sendRegisterRequest(requestBody)
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    public void tearDown() {
        String requestBody = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";

        Response response = loginUser(requestBody);
        String token = response.jsonPath().getString("accessToken");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            deleteUser(token);
        }
    }

    @Step("Регистрация пользователя с email: {email}, password: {password}, name: {name}")
    public Response registerUser(String email, String password, String name) {
        String requestBody = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"name\": \"" + name + "\"\n" +
                "}";
        return sendRegisterRequest(requestBody);
    }

    @Step("Отправка запроса на регистрацию пользователя")
    public Response sendRegisterRequest(String requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response loginUser(String requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя с accessToken")
    public void deleteUser(String token) {
        given()
                .auth().oauth2(token)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }
}
