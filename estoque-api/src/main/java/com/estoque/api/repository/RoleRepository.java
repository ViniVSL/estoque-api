package com.estoque.api.repository;

import com.estoque.api.entity.Role;
import com.estoque.api.entity.Erole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// O tipo do ID deve corresponder ao da entidade Role
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Erole name);
}