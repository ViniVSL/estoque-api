package com.estoque.api.data;

import com.estoque.api.entity.Erole;
import com.estoque.api.entity.Role;
import com.estoque.api.entity.User;
import com.estoque.api.repository.RoleRepository;
import com.estoque.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. GARANTIR A EXISTÊNCIA DAS ROLES

        Role adminRole = createRoleIfNotFound(Erole.ROLE_ADMIN);
        Role userRole = createRoleIfNotFound(Erole.ROLE_USER);

        // 2. CRIAR O USUÁRIO ADMIN (Se ainda não existir)

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            // Use uma senha que você vai lembrar para o teste final
            admin.setPassword(passwordEncoder.encode("senha123"));
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
            System.out.println("Usuário 'admin' criado com sucesso.");
        }

        // 3. CRIAR UM USUÁRIO COMUM (Se ainda não existir)

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Usuário 'user' criado com sucesso.");
        }
    }

    private Role createRoleIfNotFound(Erole name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role(name);
            System.out.println("Role criada: " + name.name());
            return roleRepository.save(role);
        });
    }
}