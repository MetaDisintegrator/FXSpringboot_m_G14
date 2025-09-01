package controller;

import jakarta.servlet.http.HttpSession;

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
    private HttpSession session;
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        hotelService = Mockito.mock(HotelService.class);
        hotelController = new HotelController(hotelService);
        session = Mockito.mock(HttpSession.class);
        bindingResult = Mockito.mock(BindingResult.class);
    }

    // 正向：参数正确且已登录
    @Test
    public void testSearchHotel_success() {
        SearchHotelRequest req = new SearchHotelRequest();
        req.setDestination("北京");
        req.setNamePattern("豪华");

        // 模拟无错误和已登录
        when(bindingResult.hasErrors()).thenReturn(false);
        when(session.getAttribute("user")).thenReturn(new Object());

        List<HotelSearchResult> results = Collections.singletonList(new HotelSearchResult());
        when(hotelService.searchHotels(anyString(), anyString())).thenReturn(results);

        ResponseEntity<?> response = hotelController.searchHotel(req, bindingResult, session);
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("查询成功", body.get("message"));
        assertEquals(results, body.get("data"));
    }

    // 反向：未登录
    @Test
    public void testSearchHotel_notLoggedIn() {
        SearchHotelRequest req = new SearchHotelRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(session.getAttribute("user")).thenReturn(null);

        ResponseEntity<?> response = hotelController.searchHotel(req, bindingResult, session);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }
}