package com.ifri.bookmyhall.configs;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
/**
 * Handler de succès d'authentification pour redirection vers le dashboard
 * approprié.
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /** Redirige l'utilisateur selon son rôle après une connexion réussie. */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        response.sendRedirect(isAdmin ? "/admin/dashboard" : "/user/dashboard");
    }
}
