package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import requests.LoginRequest;

import static io.restassured.RestAssured.given;

public class AuthSteps {

    @Step("Логин и получение токена")
    public String loginAndGetAccessToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(200);
        return loginResponse.jsonPath().getString("accessToken");
    }
}
