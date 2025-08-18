package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import requests.LoginRequest;
import requests.SignUpRequest;

import static io.restassured.RestAssured.given;

public class CreateUserSteps {

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
