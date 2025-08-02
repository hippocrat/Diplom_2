import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrderTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static String accessToken;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;

        String email = "hilokea@yandex.ru";
        String password = "kassian";

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(200);
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getOrdersWithAuthShouldReturn200() {
         given()
                .header("Authorization", accessToken)
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

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void getOrdersWithoutAuthShouldReturn401() {
        given()
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
