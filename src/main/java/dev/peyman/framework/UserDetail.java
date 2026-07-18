package dev.peyman.framework;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;

public record UserDetail(String username, Claims claims, Collection<? extends GrantedAuthority> authorities) {
}
