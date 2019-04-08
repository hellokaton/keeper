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
package io.github.biezhi.keeper.core.web.filter;

import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.subject.Subject;
import io.github.biezhi.keeper.exception.ExpiredException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtAuthenticFilter extends AuthenticFilter {

    @Override
    protected void authenticError(Exception e, HttpServletRequest request, HttpServletResponse response) {
        if (e instanceof ExpiredException) {
            Subject subject = Keeper.getSubject();
            if (null == subject || !subject.renew()) {
                this.unAuthentic(request, response);
            }
        } else {
            log.error("authentic error", e);
            this.unAuthentic(request, response);
        }
    }

}
