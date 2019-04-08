package com.example.keeper.config;

import com.example.keeper.model.Response;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.web.filter.JwtAuthenticFilter;
import io.github.biezhi.keeper.enums.SubjectType;
import io.github.biezhi.keeper.utils.WebUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class KeeperBeanConfig {

    @Bean
    public JwtAuthenticFilter jwtAuthenticFilter() {
        return new JwtAuthenticFilter() {

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

    @Bean
    @Primary
    public Keeper initKeeper(Keeper keeper){
        keeper.setSubjectType(SubjectType.JWT);
        return keeper;
    }

}
