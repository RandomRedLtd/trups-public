package eu.randomred.trupsbackend.configuration;

import eu.randomred.trupsbackend.service.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class ApiKeyFilter extends OncePerRequestFilter {

    private final AppUserService appUserService;

    @Autowired
    public ApiKeyFilter(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-Api-Key");

        if (apiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails;

        try {
            userDetails = appUserService.loadUserByUsername(apiKey);
        } catch (RuntimeException e) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(newContext);

        filterChain.doFilter(request, response);
    }

}
