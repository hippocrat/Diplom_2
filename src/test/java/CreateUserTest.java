import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import requests.SignUpRequest;

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
        SignUpRequest signUpRequest = new SignUpRequest(password, name);
        sendRegisterRequest(signUpRequest)
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    public void tearDown() {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Response response = loginUser(loginRequest);
        String token = response.jsonPath().getString("accessToken");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            deleteUser(token);
        }
    }

    @Step("Регистрация пользователя с email: {email}, password: {password}, name: {name}")
    public Response registerUser(String email, String password, String name) {
        SignUpRequest signUpRequest = new SignUpRequest(email, password, name);
        return sendRegisterRequest(signUpRequest);
    }

    @Step("Отправка запроса на регистрацию пользователя")
    public Response sendRegisterRequest(SignUpRequest requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response loginUser(LoginRequest requestBody) {
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
