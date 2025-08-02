import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest {

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
    public void testCreateOrderWithAuthorizationAndValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );

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

    @Test
    public void testCreateOrderWithoutAuthorizationWithValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );

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

    @Test
    public void testCreateOrderWithAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();

        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithoutAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        OrderRequest orderRequest = new OrderRequest(List.of("61c0c5a71d1f82001bdaaa700"));

        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(500);
    }
}
