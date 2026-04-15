package org.example.product.controller;

import org.example.common.Result;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        String nickname = params.get("nickname");
        String password = params.get("password");
        String email = params.get("email");
        if (nickname == null || password == null || email == null) {
            return Result.error(400, "参数不完整");
        }
        boolean success = userService.register(nickname, password, email);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error(400, "邮箱已存在");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String password = params.get("password");
        if (email == null || password == null) {
            return Result.error(400, "参数不完整");
        }
        String token = userService.login(email, password);
        if (token != null) {
            return Result.success(token);
        } else {
            return Result.error(401, "邮箱或密码错误");
        }
    }
}