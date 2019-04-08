package com.example.keeper.controller;

import com.example.keeper.model.Response;
import com.example.keeper.model.User;
import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.Keeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<String> login(String username, String password) {

        User user = userService.login(username, password);

        String token = Keeper.getSubject().login(user::getUsername);

        log.info("create token: {}", token);
        return Response.<String>builder().code(200).data(token).build();
    }

    /**
     * 无需登录访问
     *
     * @return
     */
    @GetMapping("/guest")
    public Response<String> guest() {
        return Response.<String>builder().code(200).data("guest!").build();
    }

    /**
     * 需登陆后访问
     *
     * @return
     */
    @GetMapping("/hello")
    public Response<String> hello() {
        return Response.<String>builder().code(200).data("i,m hello!").build();
    }

    @RequestMapping("/logout")
    public Response<String> logout() {
        Keeper.getSubject().logout();
        return Response.<String>builder().code(200).data("注销成功").build();
    }

}
