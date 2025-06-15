package com.datnt.moviebooker;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testInsertUser() {
        User user = User.builder()
                .username("admin")
                .password("password123")
                .email("datnt@gmail.com")
                .role(Role.ROLE_ADMIN)
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
}
