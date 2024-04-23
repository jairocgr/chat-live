package io.chat.live.exception;

public class UserNotFoundException extends RecordNotFoundException {

    public UserNotFoundException(String login) {
        super("User \"%s\" not found".formatted(login));
    }
}
