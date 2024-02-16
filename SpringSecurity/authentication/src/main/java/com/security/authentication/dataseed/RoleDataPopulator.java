package com.security.authentication.dataseed;

import com.security.authentication.repository.RoleRepository;
import com.security.common.enums.ERole;
import com.security.common.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Order(1)
public class RoleDataPopulator implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        ERole[] roles = ERole.values();
        for (ERole value : Arrays.stream(ERole.values()).collect(Collectors.toList())) {
            var roleByName = roleRepository.findByName(value.getValue());
            if (roleByName.isEmpty()) {
                var role = new Role();
                role.setName(value.getValue());
                roleRepository.save(role);
            }
        }
    }
}
