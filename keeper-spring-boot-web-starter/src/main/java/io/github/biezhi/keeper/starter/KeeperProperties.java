package io.github.biezhi.keeper.starter;

import io.github.biezhi.keeper.core.config.JwtConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@ConfigurationProperties("keeper")
public class KeeperProperties {

    private JwtConfig jwt = new JwtConfig();

}
