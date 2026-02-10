package user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import user_service.service.CustomUserDetailsService;
import user_service.service.JWTService;
import java.io.IOException;

@Component
public class JWtfilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
             token = authHeader.substring(7);
             username = jwtService.extractUserName(token);
        }

        //if the user is already not authenticated and the username is not null
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                //if the token is valid, set the security context
                //this will allow the user to access the endpoints
                //without having to login again

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
