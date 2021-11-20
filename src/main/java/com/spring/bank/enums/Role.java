package com.spring.bank.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
//    ADMIN(Set.of(Permission.USERS_READ, Permission.USERS_WRITE)),
    ADMIN(new HashSet<>(Arrays.asList(Permission.USERS_READ, Permission.USERS_WRITE))),
//    USER(Set.of(Permission.USERS_READ));
    USER(new HashSet<>(Arrays.asList(Permission.USERS_READ)));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions=permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}