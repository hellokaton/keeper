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
package io.github.biezhi.keeper.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author biezhi
 * @date 2019-04-04
 */
@Slf4j
@UtilityClass
public class WebUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ThreadLocal<HttpServletRequest>  REQUEST_THREAD_LOCAL  = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> RESPONSE_THREAD_LOCAL = new ThreadLocal<>();

    public static void initContext(HttpServletRequest request, HttpServletResponse response) {
        REQUEST_THREAD_LOCAL.set(request);
        RESPONSE_THREAD_LOCAL.set(response);
    }

    public static void removeRequest() {
        REQUEST_THREAD_LOCAL.remove();
        RESPONSE_THREAD_LOCAL.remove();
    }

    public HttpServletResponse currentResponse() {
        return RESPONSE_THREAD_LOCAL.get();
    }

    public HttpServletRequest currentRequest() {
        return REQUEST_THREAD_LOCAL.get();
    }

    public HttpSession currentSession() {
        return currentSession(false);
    }

    public HttpSession currentSession(boolean create) {
        HttpServletRequest request = currentRequest();
        return null != request ? request.getSession(create) : null;
    }

    public void writeJSON(HttpServletResponse response, Object data) {
        try {
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(data));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Write to response error", e);
        }
    }

}
