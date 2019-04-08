package com.example.keeper.config;

import com.example.keeper.model.Response;
import io.github.biezhi.keeper.core.web.filter.AuthenticFilter;
import io.github.biezhi.keeper.utils.WebUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class KeeperBeanConfig {

    @Bean
    public AuthenticFilter authenticFilter() {
        return new AuthenticFilter() {
            @Override
            protected void initFilterBean() {
                this.addPathPatterns("/**")
                        .excludePathPatterns("/guest", "/login");
            }

            @Override
            protected void unAuthentic(HttpServletRequest request, HttpServletResponse response) {
                WebUtil.writeJSON(response,
                        Response.<String>builder().code(500)
                                .msg("请登录后访问")
                                .build());
            }
        };
    }

}
