package com.todo.backend.config;

import com.todo.backend.utils.auth.JwtUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        // set authentication context from the jwt token in the request header if present
        final String authorizationHeader = req.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(req, res);
            return;
        }

        var jwt = authorizationHeader.substring(7); // remove Bearer prefix
        try {
            var user = jwtUtils.extractAccessToken(jwt);
            var authorities = List.of(new SimpleGrantedAuthority(user.userRole().name()));
            var userDetails = new User(user.userId(), "fuck java", authorities);
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            // invalid token, return 401
            handleUnauthorizedResponse(res, "Invalid JWT token");
            return;
        } catch (UsernameNotFoundException e) {
            handleUnauthorizedResponse(res, "User no longer exists");
            return;
        } catch (Exception e) {
            log.error("Error while setting user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(req, res);
    }

    private void handleUnauthorizedResponse(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\": \"" + message + "\"}");
        res.getWriter().flush();
        res.getWriter().close();
    }
}
