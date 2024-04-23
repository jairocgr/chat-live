package io.chat.live.repository;

import io.chat.live.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String user);

    @Query("SELECT count(*) >= ?1 FROM User u")
    boolean userLimitReached(int maxUsersAllowed);
}
