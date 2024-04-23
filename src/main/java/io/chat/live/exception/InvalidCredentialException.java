package io.chat.live.exception;

public class InvalidCredentialException extends BusinessException {
    public InvalidCredentialException() {
        super("Invalid credential");
    }
}
