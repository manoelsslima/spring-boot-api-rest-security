package br.com.alura.forum.config.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Does the authentication using a JWT token.
 */
public class AutenticacaoViaTokeFilter extends OncePerRequestFilter {
    private TokenAPIService tokenApiService;

    public AutenticacaoViaTokeFilter(TokenAPIService tokenApiService) {
        this.tokenApiService = tokenApiService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);
        boolean valido = this.tokenApiService.isTokenValido(token);
        System.out.println("Token válido: " + valido);
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        // o 7 caracter após o Bearer<espaço>
        return token.substring(7, token.length());
    }
}
