package steps;

import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class GetOrderSteps {

    @Step("Отправка запроса на получение заказов с токеном авторизации")
    public void getOrdersWithAuth(String token) {
        given()
                .header("Authorization", token)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("total", greaterThanOrEqualTo(0))
                .body("totalToday", greaterThanOrEqualTo(0));
    }

    @Step("Отправка запроса на получение заказов без авторизации")
    public void getOrdersWithoutAuth() {
        given()
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
