package controller;

import jakarta.servlet.http.HttpSession;
import org.fxtravel.services.train.controller.TrainMealController;
import org.fxtravel.fxspringboot.pojo.entities.User;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainMealControllerTest {

    @InjectMocks
    private TrainMealController controller;
    @Mock
    private TrainMealService trainMealService;
    @Mock
    private HttpSession session;


    // 正向：已登录，返回餐食列表
    @Test
    public void testGetUserMeals_success() {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);

        List<TrainMeal> meals = Collections.singletonList(new TrainMeal());
        when(trainMealService.getMealsByTrain4User(123)).thenReturn(meals);

        ResponseEntity<?> resp = controller.getUserMeals(123, session);
        assertEquals(200, resp.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("查询成功", body.get("message"));
        assertEquals(meals, body.get("data"));
        assertEquals("price", body.get("sortBy"));
    }

    // 反向：未登录
    @Test
    public void testGetUserMeals_notLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);

        ResponseEntity<?> resp = controller.getUserMeals(123, session);
        assertEquals(401, resp.getStatusCodeValue());
        assertTrue(((Map<?, ?>) resp.getBody()).containsKey("error"));
    }

    // 反向：服务层异常
    @Test
    public void testGetUserMeals_serviceException() {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);

        when(trainMealService.getMealsByTrain4User(anyInt()))
                .thenThrow(new RuntimeException("数据库异常"));

        assertThrows(RuntimeException.class, () -> controller.getUserMeals(123, session));
    }
}