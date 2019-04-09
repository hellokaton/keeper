package com.example.keeper.config;

import com.example.keeper.model.Response;
import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.cache.redis.AuthorizeRedisCache;
import io.github.biezhi.keeper.core.cache.redis.JwtSubjectRedisStorage;
import io.github.biezhi.keeper.core.web.filter.JwtAuthenticFilter;
import io.github.biezhi.keeper.enums.SubjectType;
import io.github.biezhi.keeper.utils.WebUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Configuration
public class KeeperBeanConfig extends WebMvcConfigurationSupport {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Bean
    public AuthorizeRedisCache authorizeRedisCache(StringRedisTemplate stringRedisTemplate) {
//        return new AuthorizeRedisCache(stringRedisTemplate, Duration.ofMinutes(10));
        return new AuthorizeRedisCache(stringRedisTemplate, Duration.ofSeconds(10));
    }

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
    public KeeperAuthorizeBean keeperAuthorizeBean(UserService userService, StringRedisTemplate stringRedisTemplate) {
        return new KeeperAuthorizeBean(userService, stringRedisTemplate);
    }

    @Bean
    @Primary
    public Keeper initKeeper(Keeper keeper, StringRedisTemplate stringRedisTemplate) {
        keeper.setSubjectType(SubjectType.JWT);
        keeper.setSubjectStorage(new JwtSubjectRedisStorage(stringRedisTemplate));
        return keeper;
    }

}
