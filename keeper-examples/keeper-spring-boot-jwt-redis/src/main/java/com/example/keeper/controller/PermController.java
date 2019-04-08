package com.example.keeper.controller;

import com.example.keeper.model.Response;
import io.github.biezhi.keeper.annotation.Permissions;
import io.github.biezhi.keeper.annotation.Roles;
import io.github.biezhi.keeper.enums.Logical;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PermController {

    // 拥有 admin 角色或 role 角色即可访问
    @GetMapping("/role1")
    @Roles(value = {"admin", "role"}, logical = Logical.OR)
    public Response<String> role1() {
        return Response.<String>builder().data("角色111").build();
    }

    // 拥有 sys 角色可以访问
    @GetMapping("/role2")
    @Roles("sys")
    public Response<String> role2() {
        return Response.<String>builder().data("角色222").build();
    }

    // 拥有 user:view 权限可以访问
    @GetMapping("/perm1")
    @Permissions("user:view")
    public Response<String> perm1() {
        return Response.<String>builder().data("perm1 111").build();
    }

    // 拥有 sys:view 权限可以访问
    @GetMapping("/perm2")
    @Permissions("sys:view")
    public Response<String> perm2() {
        return Response.<String>builder().data("perm2 222").build();
    }

}
