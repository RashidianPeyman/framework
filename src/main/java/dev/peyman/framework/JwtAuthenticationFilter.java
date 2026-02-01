package dev.peyman.framework;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtProperties properties;
    private final ObjectProvider<UserStatusChecker> statusChecker; // استفاده از ObjectProvider برای اختیاری بودن
    private final ObjectProvider<RequestSessionTracker> sessionTrackerProvider;
    public JwtAuthenticationFilter(JwtService jwtService, JwtProperties properties,ObjectProvider<UserStatusChecker> statusChecker,ObjectProvider<RequestSessionTracker> sessionTrackerProvider) {
        this.jwtService = jwtService;
        this.properties = properties;
        this.statusChecker = statusChecker;
        this.sessionTrackerProvider = sessionTrackerProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(properties.getHeader());

        if (authHeader != null && authHeader.startsWith(properties.getPrefix())) {
            String token = authHeader.substring(properties.getPrefix().length());
            try {
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    boolean isValid = statusChecker.getIfAvailable(() -> u -> true).isUserValid(username);
                    if (!isValid) {
                        SecurityContextHolder.clearContext();
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "User account is disabled or banned");
                        return;
                    }
                    String userAgent = request.getHeader("User-Agent");
                    String remoteIp = request.getRemoteAddr();


                    // فراخوانی ترکر (اگر در ماژولی تعریف شده باشد)
                    sessionTrackerProvider.ifAvailable(tracker ->
                            tracker.trackSession(username, userAgent, remoteIp, token)
                    );
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetail userDetail = new UserDetail(username, new  ArrayList<>());
                  //  UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.authorities());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.authorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}