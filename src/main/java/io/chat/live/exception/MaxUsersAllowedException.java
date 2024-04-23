package io.chat.live.exception;

public class MaxUsersAllowedException extends BusinessException {
    public MaxUsersAllowedException(int maxAllowed) {
        super("We've reached the max number of users allowed (limit %s)".formatted(maxAllowed));
    }
}
