package tests;

import configs.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import requests.SignUpRequest;
import steps.CreateUserSteps;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest extends BaseTest {

    String email = "tteesstt-data@yandex.ru";
    String password = "kassian";
    String name = "WhiteWolf";

    private CreateUserSteps userSteps;

    @BeforeEach
    public void init() {
        userSteps = new CreateUserSteps();
    }

    @Test
    @DisplayName("Создание нового уникального пользователя, ожидаем ответ 200")
    public void createNewUserSuccess() {
        userSteps.registerUser(email, password, name)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание пользователя который уже есть в системе, ожидаем ответ 403")
    public void createUserAlreadyExistsReturnsError() {
        userSteps.registerUser(email, password, name);
        userSteps.registerUser(email, password, name)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email, ожидаем ответ 403")
    public void createUserWithoutEmailReturnsError() {
        SignUpRequest signUpRequest = new SignUpRequest(password, name);
        userSteps.sendRegisterRequest(signUpRequest)
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    public void tearDown() {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Response response = userSteps.loginUser(loginRequest);
        String token = response.jsonPath().getString("accessToken");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userSteps.deleteUser(token);
        }
    }
}
