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
package io.github.biezhi.keeper.core.subject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.annotation.Permissions;
import io.github.biezhi.keeper.annotation.Roles;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.Authorization;
import io.github.biezhi.keeper.core.authc.AuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.enums.Logical;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author biezhi
 * @date 2019-04-05
 */
@Data
public abstract class SimpleSubject implements Subject {

    protected AuthenticInfo authenticInfo;

    @Override
    public AuthenticInfo authenticInfo() {
        return authenticInfo;
    }

    @JsonIgnore
    @Override
    public boolean hasPermissions(Roles roleAnnotation, Permissions permAnnotation) {
        AuthorizeInfo authorizeInfo = this.authorize(false);
        if (null == authorizeInfo) {
            return false;
        }
        if (null != roleAnnotation && null != authorizeInfo.getRoles()) {
            if (Logical.AND.equals(roleAnnotation.logical())) {
                return rolesAnd(authorizeInfo.getRoles(), roleAnnotation.value());
            }
            if (Logical.OR.equals(roleAnnotation.logical())) {
                String[] value = roleAnnotation.value();
                for (String s : value) {
                    return authorizeInfo.getRoles().contains(s);
                }
            }
        }
        if (null != permAnnotation && null != authorizeInfo.getPermissions()) {
            if (Logical.AND.equals(permAnnotation.logical())) {
                return permissionsAnd(authorizeInfo.getPermissions(), permAnnotation.value());
            }
            if (Logical.OR.equals(permAnnotation.logical())) {
                String[] value = permAnnotation.value();
                for (String s : value) {
                    return authorizeInfo.getPermissions().contains(s);
                }
            }
        }
        return false;
    }

    @JsonIgnore
    @Override
    public void refreshAuthorize() {
        this.authorize(true);
    }

    protected AuthorizeInfo authorize(boolean reload) {
        AuthenticInfo  authenticInfo = this.authenticInfo();
        Authorization  authorization = SpringContextUtil.getBean(Keeper.class).getAuthorization();
        String         username      = authenticInfo.username();
        AuthorizeCache cache         = authorization.loadWithCache();
        if (cache == null) {
            return authorization.doAuthorization(authenticInfo);
        }

        if (reload) {
            cache.remove(username);
        }
        if (!cache.cached(username)) {
            AuthorizeInfo authorizeInfo = authorization.doAuthorization(authenticInfo);
            if (null != authorizeInfo) {
                cache.put(username, authorizeInfo);
            }
            return authorizeInfo;
        } else {
            return cache.getAuthorizeInfo(username);
        }
    }

    private boolean rolesAnd(Set<String> roles, String[] value) {
        List<String> roleList = Arrays.asList(value);
        return roles.containsAll(roleList);
    }

    private boolean permissionsAnd(Set<String> permissions, String[] value) {
        List<String> roleList = Arrays.asList(value);
        return permissions.containsAll(roleList);
    }

}
