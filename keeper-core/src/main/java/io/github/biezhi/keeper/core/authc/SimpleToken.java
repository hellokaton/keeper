package io.github.biezhi.keeper.core.authc;

import lombok.Data;

import java.util.Map;

/**
 * @author biezhi
 * @date 2019-04-07
 */
@Data
public class SimpleToken implements AuthorToken {

    private long loginTime;

    private String username;
    private Object payload;
    private Map<String, Object> claims;

    @Override
    public String username() {
        return username;
    }

    @Override
    public Object payload() {
        return this.payload;
    }

    @Override
    public Map<String, Object> claims() {
        return this.claims;
    }

}
