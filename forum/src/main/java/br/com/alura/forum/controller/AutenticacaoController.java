package br.com.alura.forum.controller;

import br.com.alura.forum.config.security.TokenAPIService;
import br.com.alura.forum.controller.dto.TokenDTO;
import br.com.alura.forum.controller.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Substitui o controller nativo do spring security uma vez que mudamos
 * a autenticação de sessão para token.
 */
@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    // dispara o processo de autenticação
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenAPIService tokenAPIService;

    @PostMapping
    public ResponseEntity<TokenDTO> autenticar(@RequestBody @Valid LoginForm form) {
        try {
            UsernamePasswordAuthenticationToken dadosLogin = form.converter();
            Authentication authentication = authenticationManager.authenticate(dadosLogin);
            String token = this.tokenAPIService.gerarToken(authentication);

            return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
