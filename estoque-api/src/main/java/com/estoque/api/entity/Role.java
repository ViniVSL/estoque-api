package com.estoque.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // O tipo de ID deve ser o mesmo usado no seu JpaRepository

    @Enumerated(EnumType.STRING) // Mapeia o Enum como String no banco
    @Column(length = 20)
    private Erole name;

    public Role() {
    }

    public Role(Erole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Erole getName() {
        return name;
    }

    public void setName(Erole name) {
        this.name = name;
    }
}