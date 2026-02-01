package dev.peyman.framework;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserDetail(String username,
                         Collection<? extends GrantedAuthority> authorities) {
}
