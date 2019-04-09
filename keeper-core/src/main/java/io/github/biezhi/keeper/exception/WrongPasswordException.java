package io.github.biezhi.keeper.exception;

import lombok.NoArgsConstructor;

import static io.github.biezhi.keeper.keeperConst.ERROR_MESSAGE_WRONG_PASSWORD;

/**
 * WrongPasswordException
 *
 * @author biezhi
 * @since 2019/4/9
 */
@NoArgsConstructor
public class WrongPasswordException extends UnauthenticException {

    public WrongPasswordException(String message) {
        super(message);
    }

    public static WrongPasswordException build() {
        return build(ERROR_MESSAGE_WRONG_PASSWORD);
    }

    public static WrongPasswordException build(String msg) {
        return new WrongPasswordException(msg);
    }


}
