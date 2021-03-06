package org.generation.blogPessoal.Service;

import java.nio.charset.Charset;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServices {
	
	private @Autowired UsuarioRepository repository;
	
	@SuppressWarnings("null")
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		CrudRepository<Usuario, Long> usuarioRepository = null;
		if (usuarioRepository.findById(usuario.getId()).isPresent()) {
			Optional<Usuario> buscaUsuario = ((UsuarioRepository) usuarioRepository)
					.findByUsuario(usuario.getUsuario());

			if (buscaUsuario.isPresent()) {
				if (buscaUsuario.get().getId() != usuario.getId())
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
			}

			usuario.setSenha(criptografarSenha(usuario.getSenha()));
			return Optional.of(usuarioRepository.save(usuario));
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!", null);
	}

	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String senhaEncoder = encoder.encode(senha);

		return senhaEncoder;
	}

	public ResponseEntity<Usuario> cadastrarUsuario(@Valid Usuario usuario) {
		Optional<Usuario> optional = repository.findByUsuario(usuario.getUsuario());

		if (optional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ja existente, tente outro usuario!.");
		} else {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String senhaCriptografada = encoder.encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
			return ResponseEntity.status(201).body(repository.save(usuario));
		}
	}

	public ResponseEntity<UserLogin> logar(@Valid Optional<UserLogin> user) {
		return repository.findByUsuario(user.get().getUsuario()).map(resp -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			if (encoder.matches(user.get().getSenha(), resp.getSenha())) {

				String estrutura = user.get().getUsuario() + ":" + user.get().getSenha(); // julia@email.com:134652
				byte[] estruturaBase64 = Base64.encodeBase64(estrutura.getBytes(Charset.forName("US-ASCII"))); // YGUIJHGyuHGKJH116573
				String basicToken = "Basic " + new String(estruturaBase64);// Basic YGUIJHGyuHGKJH116573

				UserLogin credenciais = new UserLogin();
				credenciais.setToken(basicToken);
				credenciais.setId(resp.getId());
				credenciais.setNome(resp.getNome());
				credenciais.setSenha(resp.getSenha());
				credenciais.setUsuario(resp.getUsuario());
				credenciais.setFoto(resp.getFoto());
				credenciais.setTipo(resp.getTipo());
				return ResponseEntity.ok(credenciais);

			} else {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha invalida.");
			}
		}).orElseThrow(() -> {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email invalido.");
		});
	}

}
