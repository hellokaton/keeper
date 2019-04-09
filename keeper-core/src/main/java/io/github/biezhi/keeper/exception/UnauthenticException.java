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
package io.github.biezhi.keeper.exception;

import lombok.NoArgsConstructor;

import static io.github.biezhi.keeper.KeeperConst.ERROR_MESSAGE_NOT_LOGIN;

@NoArgsConstructor
public class UnauthenticException extends KeeperException {

    public UnauthenticException(String message) {
        super(message);
    }

    public static UnauthenticException build() {
        return build(ERROR_MESSAGE_NOT_LOGIN);
    }

    public static UnauthenticException build(String msg) {
        return new UnauthenticException(msg);
    }

}
