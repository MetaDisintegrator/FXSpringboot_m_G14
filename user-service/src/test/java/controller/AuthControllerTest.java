package controller;

import org.fxtravel.services.user.controller.AuthController;
import org.fxtravel.services.user.dto.*;
import org.fxtravel.services.user.entity.User;
import org.fxtravel.services.user.service.inter.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;
    @Mock
    private UserService userService;
    @Mock
    private HttpSession session;

    @Test
    void registerAccount_success() {
        RegisterRequest request = new RegisterRequest();
        User user = new User();
        Mockito.when(userService.register(request)).thenReturn(user);

        ResponseEntity<?> response = authController.registerAccount(request, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("注册成功", ((java.util.Map<?,?>)response.getBody()).get("message"));
        Mockito.verify(session).setAttribute("user", user);
    }

    @Test
    void registerAccount_fail() {
        RegisterRequest request = new RegisterRequest();
        Mockito.when(userService.register(request)).thenReturn(null);

        ResponseEntity<?> response = authController.registerAccount(request, session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("邮箱已注册", ((java.util.Map<?,?>)response.getBody()).get("error"));
        Mockito.verify(session).setAttribute("user", null);
    }

    // 正向测试：登录成功
    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        User user = new User();
        Mockito.when(userService.login(request.getEmail(), request.getPassword())).thenReturn(user);

        ResponseEntity<?> response = authController.login(request, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("登录成功", ((java.util.Map<?,?>)response.getBody()).get("message"));
        Mockito.verify(session).setAttribute("user", user);
    }

    // 反向测试：登录失败
    @Test
    void login_fail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");
        Mockito.when(userService.login(request.getEmail(), request.getPassword())).thenReturn(null);

        ResponseEntity<?> response = authController.login(request, session);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("邮箱或密码错误", ((java.util.Map<?,?>)response.getBody()).get("error"));
        Mockito.verify(session, Mockito.never()).setAttribute(Mockito.eq("user"), Mockito.any());
    }

}
