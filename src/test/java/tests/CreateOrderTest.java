package tests;

import configs.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.OrderRequest;
import steps.AuthSteps;
import steps.OrderSteps;

import java.util.List;

public class CreateOrderTest extends BaseTest {

    private static String accessToken;
    private OrderSteps orderSteps;

    @BeforeAll
    public static void setup() {
        AuthSteps authSteps = new AuthSteps();
        accessToken = authSteps.loginAndGetAccessToken("hilokea@yandex.ru", "kassian");
    }

    @BeforeEach
    public void init() {
        orderSteps = new OrderSteps(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    public void testCreateOrderWithAuthorizationAndValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );
        orderSteps.createOrderWithAuth(orderRequest);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и валидными ингредиентами")
    public void testCreateOrderWithoutAuthorizationWithValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(
                List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c")
        );
        orderSteps.createOrderWithoutAuth(orderRequest);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    public void testCreateOrderWithAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();
        orderSteps.createInvalidOrderWithAuth(orderRequest, 400, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа без авторизации без ингредиентов")
    public void testCreateOrderWithoutAuthorizationWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest();
        orderSteps.createInvalidOrderWithoutAuth(orderRequest, 400, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с невалидными зэшами ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        OrderRequest orderRequest = new OrderRequest(List.of("61c0c5a71d1f82001bdaaa700"));
        orderSteps.createOrderExpectingStatusWithAuth(orderRequest, 500);
    }
}
