package org.generation.blogPessoal.Controller;

import java.util.Optional;

import javax.validation.Valid;

import org.generation.blogPessoal.Service.UsuarioServices;
import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

	@Autowired
	private UsuarioServices services;

	@PostMapping("/logar")
	public ResponseEntity<UserLogin> Autentication(@Valid @RequestBody Optional<UserLogin> user) {
		return services.logar(user);

	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> Post(@Valid @RequestBody Usuario usuario) {
		return services.cadastrarUsuario(usuario);
	}

}
