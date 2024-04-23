package io.chat.live.exception;

public class CantJoinTwiceException extends BusinessException {
    public CantJoinTwiceException() {
        super("Can not join the same room twice");
    }
}
