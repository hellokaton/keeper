package io.github.biezhi.keeper.starter;

import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.aspect.KeeperAspect;
import io.github.biezhi.keeper.core.authc.Authorization;
import io.github.biezhi.keeper.core.jwt.JwtToken;
import io.github.biezhi.keeper.core.jwt.SimpleJwtToken;
import io.github.biezhi.keeper.enums.SubjectType;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeeperProperties.class)
public class AuthedAutoConfiguration {

    @Autowired
    private KeeperProperties keeperProperties;

    @Bean
    public KeeperAspect keeperAspect() {
        return new KeeperAspect();
    }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public JwtToken jwtToken() {
        return new SimpleJwtToken(keeperProperties.getJwt());
    }

    @Bean
    public Keeper keeper(@Autowired(required = false) Authorization authorization) {
        Keeper keeper = new Keeper();
        keeper.setAuthorization(authorization);
        keeper.setJwtConfig(keeperProperties.getJwt());
        // default subject type
        keeper.setSubjectType(SubjectType.SESSION);
        return keeper;
    }

}
