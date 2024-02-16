package com.security.common.model;

import com.security.common.enums.ERole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id",nullable = false)
    private Integer id;

    @Column(name="role_name",nullable = false)
    private String name;
}
