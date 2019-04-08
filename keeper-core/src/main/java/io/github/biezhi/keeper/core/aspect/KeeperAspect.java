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
package io.github.biezhi.keeper.core.aspect;

import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.annotation.Permissions;
import io.github.biezhi.keeper.annotation.Roles;
import io.github.biezhi.keeper.core.authc.Authorization;
import io.github.biezhi.keeper.core.subject.Subject;
import io.github.biezhi.keeper.exception.UnauthenticException;
import io.github.biezhi.keeper.exception.UnauthorizedException;
import io.github.biezhi.keeper.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
public class KeeperAspect {

    private static final Map<Subject, Integer> SUBJECT_PERMISSION_CACHE = new ConcurrentHashMap<>();

    @Autowired
    private Keeper keeper;

    @Pointcut("@annotation(io.github.biezhi.keeper.annotation.Permissions) || @annotation(io.github.biezhi.keeper.annotation.Roles)")
    public void permBefore() {
    }

    @Before("permBefore()")
    public void beforePermissions(JoinPoint joinPoint) {
        Authorization authorization = keeper.getAuthorization();
        if (null == authorization) {
            throw new UnauthorizedException("Unauthorized");
        }

        Subject subject = Keeper.getSubject();
        if (null == subject || !subject.isLogin()) {
            throw UnauthenticException.build();
        }

        int hash = 0;
        if (keeper.enableURIAuthorizeCache()) {
            HttpServletRequest request = WebUtil.currentRequest();

            hash = Objects.hash(
                    subject.username(),
                    request.getMethod(),
                    request.getRequestURI());

            if (SUBJECT_PERMISSION_CACHE.containsKey(subject) &&
                    SUBJECT_PERMISSION_CACHE.get(subject).equals(hash)) {
                return;
            }
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Roles roles = method.getAnnotation(Roles.class);

        Permissions permissions = method.getAnnotation(Permissions.class);

        if (!subject.hasPermissions(roles, permissions)) {
            throw UnauthorizedException.build();
        }
        if (keeper.enableURIAuthorizeCache()) {
            SUBJECT_PERMISSION_CACHE.put(subject, hash);
        }

    }

}
