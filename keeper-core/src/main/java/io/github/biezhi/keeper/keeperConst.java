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
package io.github.biezhi.keeper;

/**
 * @author biezhi
 * @date 2019-04-04
 */
public interface keeperConst {

    String KEEPER_SESSION_KEY = "KEEPER_USER_TOKEN";

    String ERROR_MESSAGE_NOT_PERMISSION = "You don't have permission to access this resource!";
    String ERROR_MESSAGE_NOT_LOGIN      = "Please isLogin and take action!";
    String ERROR_MESSAGE_EXPIRED        = "Your account has expired!";
    String ERROR_MESSAGE_WRONG_PASSWORD = "Wrong password!";

}
