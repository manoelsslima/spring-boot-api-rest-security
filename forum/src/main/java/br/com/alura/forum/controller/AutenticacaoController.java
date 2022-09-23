package br.com.alura.forum.controller;

import br.com.alura.forum.controller.form.LoginForm;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginForm form) {
        System.out.println(form.getEmail());
        System.out.println(form.getSenha());
        return ResponseEntity.ok().build();
    }
}
