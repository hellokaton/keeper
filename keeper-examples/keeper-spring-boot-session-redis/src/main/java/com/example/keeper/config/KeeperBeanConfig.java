package com.example.keeper.config;

import com.example.keeper.model.Response;
import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.cache.redis.AuthenticRedisCache;
import io.github.biezhi.keeper.core.cache.redis.AuthorizeRedisCache;
import io.github.biezhi.keeper.core.cache.redis.LogoutRedisCache;
import io.github.biezhi.keeper.core.web.filter.AuthenticFilter;
import io.github.biezhi.keeper.enums.SubjectType;
import io.github.biezhi.keeper.utils.WebUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * @author biezhi
 * @date 2019-04-07
 */
@Configuration
public class KeeperBeanConfig {

    @Bean
    public AuthorizeRedisCache authorizeRedisCache(StringRedisTemplate stringRedisTemplate) {
        return new AuthorizeRedisCache(stringRedisTemplate, Duration.ofMinutes(10));
    }

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

    @Bean
    public KepperAuthorizeBean kepperAuthorizeBean(UserService userService, StringRedisTemplate stringRedisTemplate) {
        return new KepperAuthorizeBean(userService, stringRedisTemplate);
    }

    @Bean
    @Primary
    public Keeper initKeeper(Keeper keeper, StringRedisTemplate stringRedisTemplate) {
        keeper.setSubjectType(SubjectType.SESSION);
        keeper.setAuthenticInfoCache(new AuthenticRedisCache(stringRedisTemplate));
        return keeper;
    }

}
