package controller;

import jakarta.servlet.http.HttpSession;
import org.fxtravel.services.user.controller.UserController;
import org.fxtravel.services.user.entity.User;
import org.fxtravel.services.user.service.inter.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private HttpSession session;

    // getUserInfo
    @Test
    public void testGetUserInfo_success() {
        User user = new User();
        Mockito.when(session.getAttribute("user")).thenReturn(user);

        ResponseEntity<?> response = userController.getUserInfo(session);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testGetUserInfo_fail() {
        Mockito.when(session.getAttribute("user")).thenReturn(null);

        ResponseEntity<?> response = userController.getUserInfo(session);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }
}