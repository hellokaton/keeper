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

import java.util.Set;

/**
 * AuthorizeInfo interface
 * <p>
 * Define the selection of user authorization information,
 * including role information and permission information.
 *
 * @author biezhi
 * @date 2019-04-08
 */
public interface AuthorizeInfo {

    /**
     * @return a list of roles owned by the user, stored as a string
     */
    Set<String> getRoles();

    /**
     * @return a list of permissions owned by the user, stored as a string
     */
    Set<String> getPermissions();

}
