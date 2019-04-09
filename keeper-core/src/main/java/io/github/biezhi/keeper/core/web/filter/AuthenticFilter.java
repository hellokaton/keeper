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
import io.github.biezhi.keeper.exception.UnauthorizedException;
import io.github.biezhi.keeper.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AuthenticFilter
 *
 * @author biezhi
 * @date 2019-04-07
 */
@Slf4j
public class AuthenticFilter extends OncePerRequestFilter {

    private final List<String> includePatterns = new ArrayList<>();
    private final List<String> excludePatterns = new ArrayList<>();

    private PathMatcher pathMatcher = new AntPathMatcher();

    public AuthenticFilter addPathPatterns(String... urls) {
        return addPathPatterns(Arrays.asList(urls));
    }

    public AuthenticFilter addPathPatterns(List<String> patterns) {
        this.includePatterns.addAll(patterns);
        return this;
    }

    public AuthenticFilter excludePathPatterns(String... urls) {
        return excludePathPatterns(Arrays.asList(urls));
    }

    public AuthenticFilter excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }

    /**
     * Determine a match for the given lookup path.
     *
     * @param lookupPath  the current request path
     * @param pathMatcher a path matcher for path pattern matching
     * @return {@code true} if the interceptor applies to the given request path
     */
    protected boolean matches(String lookupPath, PathMatcher pathMatcher) {
        PathMatcher pathMatcherToUse = (this.pathMatcher != null ? this.pathMatcher : pathMatcher);
        if (!ObjectUtils.isEmpty(this.excludePatterns)) {
            for (String pattern : this.excludePatterns) {
                if (pathMatcherToUse.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        if (ObjectUtils.isEmpty(this.includePatterns)) {
            return true;
        }
        for (String pattern : this.includePatterns) {
            if (pathMatcherToUse.match(pattern, lookupPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String lookupPath = request.getRequestURI();
        // init web context
        WebUtil.initContext(request, response);

        // whether to skip the URI
        if (!this.matches(lookupPath, pathMatcher)) {
            this.doFilter(request, response, filterChain);
            WebUtil.removeRequest();
            return;
        }
        boolean authentic;
        try {
            authentic = isAuthentic(request, response);
        } catch (Exception e) {
            this.authenticError(e, request, response, filterChain);
            WebUtil.removeRequest();
            return;
        }
        if (authentic) {
            this.doFilter(request, response, filterChain);
        } else {
            this.unAuthentic(request, response);
        }
        WebUtil.removeRequest();
    }

    /**
     * Whether the authentication is passed,
     * <p>
     * return true when the user is logged in, continue the following process
     *
     * @return
     */
    protected boolean isAuthentic(HttpServletRequest request, HttpServletResponse response) {
        Subject subject = Keeper.getSubject();
        return subject.isLogin();
    }

    /**
     * Processing logic when an authentication failure occurs abnormally
     */
    protected void authenticError(Exception e, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (e instanceof ExpiredException) {
            Subject subject = Keeper.getSubject();
            if (!subject.renew()) {
                this.unAuthentic(request, response);
            } else {
                this.doFilter(request, response, filterChain);
            }
        } else {
            log.error("authentic error", e);
            this.unAuthentic(request, response);
        }
    }

    /**
     * Processing logic when not logged in or authentication failed
     */
    protected void unAuthentic(HttpServletRequest request, HttpServletResponse response) {
        throw UnauthorizedException.build();
    }

}
