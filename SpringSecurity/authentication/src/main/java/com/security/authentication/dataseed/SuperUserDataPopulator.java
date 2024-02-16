package com.security.authentication.dataseed;

import com.security.authentication.model.User;
import com.security.authentication.repository.RoleRepository;
import com.security.authentication.repository.UserRepository;
import com.security.common.enums.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(2)
public class SuperUserDataPopulator implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!this.userRepository.existsByUsername("admin@secure.com")) {
            // create the user
            final User user = new User();
            user.setId(2000L);
            user.setUsername("admin@secure.com");
            user.setPassword(this.passwordEncoder.encode("Admin@1234"));
            user.setFirstname("Super");
            user.setLastname("Administrator");
            // search and assign the SUPER_ADMIN role.
            this.roleRepository
                    .findByName(ERole.ROLE_ADMIN.getValue())
                    .ifPresent(role -> user.setRoles(Set.of(role)));
            this.userRepository.save(user);
        }
    }
}
