package tests;

import configs.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.AuthSteps;
import steps.GetOrderSteps;

public class GetOrderTest extends BaseTest {

    private static String accessToken;
    private static AuthSteps authSteps;
    private static GetOrderSteps getOrderSteps;

    @BeforeAll
    public static void setup() {
        String email = "hilokea@yandex.ru";
        String password = "kassian";
        accessToken = authSteps.loginAndGetAccessToken(email, password);
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getOrdersWithAuthShouldReturn200() {
        getOrderSteps.getOrdersWithAuth(accessToken);
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void getOrdersWithoutAuthShouldReturn401() {
        getOrderSteps.getOrdersWithoutAuth();
    }
}
