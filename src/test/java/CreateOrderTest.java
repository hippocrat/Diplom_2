import configs.BaseTest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.OrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    private static String accessToken;

    @BeforeAll
    public static void setup() {
        accessToken = loginAndGetAccessToken("hilokea@yandex.ru", "kassian");
    }

    @Step("Логин и получение токена")
    private static String loginAndGetAccessToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(200);
        return loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    public void testCreateOrderWithAuthorizationAndValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );
        createOrderWithAuth(orderRequest);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и валидными ингредиентами")
    public void testCreateOrderWithoutAuthorizationWithValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );
        createOrderWithoutAuth(orderRequest);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    public void testCreateOrderWithAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();
        createInvalidOrderWithAuth(orderRequest, 400, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа без авторизации без ингредиентов")
    public void testCreateOrderWithoutAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();
        createInvalidOrderWithoutAuth(orderRequest, 400, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с невалидными зэшами ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        OrderRequest orderRequest = new OrderRequest(List.of("61c0c5a71d1f82001bdaaa700"));
        createOrderExpectingStatusWithAuth(orderRequest, 500);
    }

    @Step("Создать заказ с авториацией")
    private void createOrderWithAuth(OrderRequest orderRequest) {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Step("Создать заказ без авторизации")
    private void createOrderWithoutAuth(OrderRequest orderRequest) {
        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Step("Создать заказ с авторизацией со статус кодом {expectedStatus}")
    private void createInvalidOrderWithAuth(OrderRequest orderRequest, int expectedStatus, String expectedMessage) {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(expectedStatus)
                .body("success", is(false))
                .body("message", equalTo(expectedMessage));
    }

    @Step("Создать заказ без авторизации со статус кодом {expectedStatus}")
    private void createInvalidOrderWithoutAuth(OrderRequest orderRequest, int expectedStatus, String expectedMessage) {
        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(expectedStatus)
                .body("success", is(false))
                .body("message", equalTo(expectedMessage));
    }

    @Step("Создать заказ с невалидными ингридиентами со статус кодом {expectedStatus}")
    private void createOrderExpectingStatusWithAuth(OrderRequest orderRequest, int expectedStatus) {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(expectedStatus);
    }
}
