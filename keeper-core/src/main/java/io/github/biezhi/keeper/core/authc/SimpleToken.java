/**
 * Copyright (c) 2019, biezhi (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.biezhi.keeper.core.authc;

import lombok.Data;

import java.util.Map;

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
