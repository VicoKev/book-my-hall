package com.ifri.bookmyhall.configs;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ADMIN"));

        String redirectUrl;
        if (isAdmin) {
            redirectUrl = "/admin/dashboard";
        } else {
            redirectUrl = "/user/dashboard";
        }

        response.sendRedirect(redirectUrl);
    }
}
