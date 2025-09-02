package org.fxtravel.services.user.controller;

import jakarta.servlet.http.HttpSession;

import org.fxtravel.services.user.entity.User;
import org.fxtravel.services.user.mapper.UserMapper;
import org.fxtravel.services.user.service.inter.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/userdata")
    public ResponseEntity<?> getUserInfo(@RequestHeader("X-User-Id") String userId) {
        try {
            int id = Integer.parseInt(userId);
            User user = userMapper.selectById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "未登录"));
            }
            return ResponseEntity.ok(user);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "无效的用户ID"));
        }
    }
}
