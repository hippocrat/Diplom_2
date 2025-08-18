package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import requests.OrderRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderSteps {

    private final String accessToken;

    public OrderSteps(String accessToken) {
        this.accessToken = accessToken;
    }

    @Step("Создать заказ с авторизацией")
    public void createOrderWithAuth(OrderRequest orderRequest) {
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
    public void createOrderWithoutAuth(OrderRequest orderRequest) {
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
    public void createInvalidOrderWithAuth(OrderRequest orderRequest, int expectedStatus, String expectedMessage) {
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
    public void createInvalidOrderWithoutAuth(OrderRequest orderRequest, int expectedStatus, String expectedMessage) {
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
    public void createOrderExpectingStatusWithAuth(OrderRequest orderRequest, int expectedStatus) {
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
