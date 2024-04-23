package io.chat.live.fixture;

import io.chat.live.domain.User;

import java.util.LinkedList;
import java.util.List;

public class UserListFixture implements Fixture<List<User>> {

    private final List<User> users;

    public UserListFixture() {
        users = new LinkedList<>();
    }

    public UserListFixture fixedList() {
        var admin = new UserFixture()
            .adminUser()
            .build();
        var joseph = new UserFixture()
            .joseph()
            .build();
        var kelly = new UserFixture()
            .kelly()
            .build();

        users.add(admin);
        users.add(kelly);
        users.add(joseph);

        return this;
    }


    @Override
    public List<User> build() {
        return users;
    }
}
