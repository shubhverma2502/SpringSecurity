package com.security.common.enums;

import lombok.Getter;

@Getter
public enum ERole {
    ROLE_ADMIN("admin"),

    ROLE_USER("user"),

    ROLE_MANAGER("manager");

    private final String value;

    ERole(String value) {
        this.value = value;
    }
}
