package controller;

import org.fxtravel.services.train.controller.TrainMealController;
import org.fxtravel.services.train.entitiy.TrainMeal;
import org.fxtravel.services.train.service.inter.TrainMealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainMealControllerTest {

    @Mock
    private TrainMealService trainMealService;

    @InjectMocks
    private TrainMealController trainMealController;

    private List<TrainMeal> mockMeals;

    @BeforeEach
    void setUp() {
        TrainMeal meal1 = new TrainMeal();
        meal1.setId(1);
        meal1.setName("Meal A");
        meal1.setPrice(25.0);

        TrainMeal meal2 = new TrainMeal();
        meal2.setId(2);
        meal2.setName("Meal B");
        meal2.setPrice(30.0);

        mockMeals = Arrays.asList(meal1, meal2);
    }

    @Test
    void getUserMeals_Success() {
        // Arrange
        when(trainMealService.getMealsByTrain4User(anyInt())).thenReturn(mockMeals);

        // Act
        ResponseEntity<?> response = trainMealController.getUserMeals("user123", 1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("查询成功", responseBody.get("message"));
        assertEquals("price", responseBody.get("sortBy"));
        assertEquals(mockMeals, responseBody.get("data"));

        verify(trainMealService, times(1)).getMealsByTrain4User(1);
    }

    @Test
    void getUserMeals_EmptyResult() {
        // Arrange
        when(trainMealService.getMealsByTrain4User(anyInt())).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = trainMealController.getUserMeals("user123", 1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("查询成功", responseBody.get("message"));
        assertEquals("price", responseBody.get("sortBy"));
        assertTrue(((List<?>) responseBody.get("data")).isEmpty());

        verify(trainMealService, times(1)).getMealsByTrain4User(1);
    }
}