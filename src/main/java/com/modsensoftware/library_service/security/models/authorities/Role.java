package com.modsensoftware.library_service.security.models.authorities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Role {
    USER(
            Set.of(
                    Permission.BREATHE
            )
    ),
    LIBRARY_WORKER(
            Set.of(
                    Permission.RETURN_BOOK_FOR_USER,
                    Permission.BORROW_BOOK_ON_USER,
                    Permission.REGISTER_LIBRARY_SERVICE_USER,
                    Permission.ADD_BOOKS_TO_LIBRARY,
                    Permission.GET_ALL_LIBRARY_BOOKS,
                    Permission.GET_LOANS_BY_USER
            )
    ),
    SECRET_KEY(
            Set.of(
                    Permission.BREATHE,
                    Permission.RETURN_BOOK_FOR_USER,
                    Permission.BORROW_BOOK_ON_USER,
                    Permission.ADD_BOOKS_TO_LIBRARY,
                    Permission.GET_ALL_LIBRARY_BOOKS,
                    Permission.REGISTER_LIBRARY_SERVICE_USER,
                    Permission.GET_LOANS_BY_USER
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority("PERMISSION_"+ permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}