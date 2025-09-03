package org.fxtravel.services.user.demo;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    // 普通 GET，任何模式都能访问
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of("msg", "hello world", "ts", System.currentTimeMillis());
    }

    // 写操作：在 readonly 模式会被拦截，返回 423；disabled 模式返回 503
    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        return Map.of("msg", "created", "data", body);
    }
}
