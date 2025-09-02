package controller;

import org.fxtravel.services.hotel.controller.HotelController;
import org.fxtravel.services.hotel.dto.HotelSearchResult;
import org.fxtravel.services.hotel.dto.SearchHotelRequest;
import org.fxtravel.services.hotel.service.inter.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class HotelControllerTest {

    private HotelController hotelController;
    private HotelService hotelService;
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        hotelService = Mockito.mock(HotelService.class);
        hotelController = new HotelController(hotelService);
        bindingResult = Mockito.mock(BindingResult.class);
    }

    // 正向测试
    @Test
    public void testSearchHotel_success() {
        SearchHotelRequest req = new SearchHotelRequest();
        req.setDestination("北京");
        req.setNamePattern("豪华");

        when(bindingResult.hasErrors()).thenReturn(false);
        List<HotelSearchResult> results = Collections.singletonList(new HotelSearchResult());
        when(hotelService.searchHotels(anyString(), anyString())).thenReturn(results);

        ResponseEntity<?> response = hotelController.searchHotel("1", req, bindingResult);
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("查询成功", body.get("message"));
        assertEquals(results, body.get("data"));
    }

    // 反向测试：参数校验失败
    @Test
    public void testSearchHotel_invalidParams() {
        SearchHotelRequest req = new SearchHotelRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = hotelController.searchHotel("1", req, bindingResult);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("errors"));
    }

    // 反向测试：服务异常
    @Test
    public void testSearchHotel_serviceException() {
        SearchHotelRequest req = new SearchHotelRequest();
        req.setDestination("北京");
        req.setNamePattern("豪华");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(hotelService.searchHotels(anyString(), anyString())).thenThrow(new RuntimeException("数据库异常"));

        ResponseEntity<?> response = hotelController.searchHotel("1", req, bindingResult);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }
}