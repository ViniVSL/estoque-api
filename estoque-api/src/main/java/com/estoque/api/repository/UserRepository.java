package com.estoque.api.repository;

import com.estoque.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Usado pelo UserDetailsServiceImpl
    Optional<User> findByUsername(String username);

    // Usado pelo AuthController para verificar se o usuário existe
    Boolean existsByUsername(String username);
}