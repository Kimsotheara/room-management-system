package com.room.management.bootstrap;

import com.room.management.entity.auth.Role;
import com.room.management.entity.auth.User;
import com.room.management.entity.auth.UserRole;
import com.room.management.repository.RoleRepository;
import com.room.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = seedAdminRole();
        seedAdminUser(adminRole);
    }

    private Role seedAdminRole() {
        return roleRepository.findByRoleNameAndIsActiveTrue("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setRoleName("ADMIN");
            role.setRoleDescription("System administrator with full access");
            role.setIsActive(true);
            Role saved = roleRepository.save(role);
            log.info("Created default ADMIN role");
            return saved;
        });
    }

    private void seedAdminUser(Role adminRole) {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setEmail("admin@roommanagement.local");
        user.setFullName("System Administrator");
        user.setIsActive(true);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(adminRole);
        userRole.setIsActive(true);

        user.setUserRoles(java.util.Set.of(userRole));

        userRepository.save(user);
        log.info("Created default admin user (username: admin, password: admin123) — change this password immediately!");
    }
}
