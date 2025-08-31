package org.fxtravel.services.user.controller;

import jakarta.servlet.http.HttpSession;
import org.fxtravel.services.user.service.inter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.fxtravel.services.user.dto.*;
import org.fxtravel.services.user.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterRequest request, HttpSession session) {
        User user = userService.register(request);

        session.setAttribute("user", user);

        if (user != null) {
            return ResponseEntity.ok(Map.of("message", "注册成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "邮箱已注册"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody VerifyRequest request) {
        boolean success = userService.verifyCode(request.getCode());
        if (success) {
            return ResponseEntity.ok().body(Map.of("message","验证成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "验证失败"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request.getEmail(), request.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "邮箱或密码错误"));
        }

        session.setAttribute("user", user);

        return ResponseEntity.ok(Map.of("message", "登录成功"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 销毁 Session
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    @PostMapping("/resetPassword/ByVerificationCode")
    public ResponseEntity<?> resetPasswordByVerificationCode(@RequestBody ResetPasswordByVerificationCodeRequest request) {
        boolean success = userService.resetPasswordByVerificationCode(request.getEmail(),request.getCode(),request.getPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message","重置密码成功"));
        } else {
            return ResponseEntity.ok(Map.of("error","验证码错误或账号未验证"));
        }
    }

    @PostMapping("/resetPassword/ByOldPassword")
    public ResponseEntity<?> resetPasswordByOldPassword(@RequestBody ResetPasswordByOldPasswordRequest request) {
        boolean success = userService.resetPasswordByOldPassword(request.getEmail(),request.getOldPassword(),request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message","重置密码成功"));
        } else {
            return ResponseEntity.ok(Map.of("error","旧密码错误或账号未验证"));
        }
    }

}
