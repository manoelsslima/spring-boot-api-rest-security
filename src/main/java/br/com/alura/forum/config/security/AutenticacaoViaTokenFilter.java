package br.com.alura.forum.config.security;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Does the authentication using a JWT token.
 */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {
    private TokenAPIService tokenApiService;
    private UsuarioRepository usuarioRepository;

    public AutenticacaoViaTokenFilter(TokenAPIService tokenApiService, UsuarioRepository usuarioRepository) {
        this.tokenApiService = tokenApiService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);
        boolean valido = this.tokenApiService.isTokenValido(token);
        if (valido) {
            autenticarCliente(token);
        }
        filterChain.doFilter(request, response);
    }

    /**
     *
     * @param token
     */
    private void autenticarCliente(String token) {
        Long idUsuario = this.tokenApiService.getIdUsuario(token);
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            // informa ao spring que o usuário já se autenticou
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
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
