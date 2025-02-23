
package com.OddinaryCosmo.UPV.Service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.OddinaryCosmo.UPV.Model.UsuarioLogin;
import com.OddinaryCosmo.UPV.Model.UsuarioModel;
import com.OddinaryCosmo.UPV.Repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Optional<UsuarioModel> cadastrarUsuario(UsuarioModel usuario){
		
		 if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			 return Optional.empty();
		 
		 usuario.setSenha(criptografarSenha(usuario.getSenha()));
		 
		 return Optional.of(usuarioRepository.save(usuario));
	}
	
	public Optional<UsuarioModel> atualizarUsuario(UsuarioModel usuario){
		
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {
			Optional<UsuarioModel> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
			
			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Usuário já existe!",null);
			
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
			
			return Optional.ofNullable(usuarioRepository.save(usuario));
		}
		return Optional.empty();
	}
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		Optional<UsuarioModel> usuario =usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if(usuario.isPresent()) {
		
		if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
			
			usuarioLogin.get().setId(usuario.get().getId());
			usuarioLogin.get().setNome(usuario.get().getNome());
			usuarioLogin.get().setSobrenome(usuario.get().getSobrenome());
			usuarioLogin.get().setCpf(usuario.get().getCpf());
			usuarioLogin.get().setTelefone(usuario.get().getTelefone());
			usuarioLogin.get().setFoto(usuario.get().getFoto());
			usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(),usuarioLogin.get().getSenha()));
			usuarioLogin.get().setSenha(usuario.get().getSenha());
			usuarioLogin.get().setTipo(usuario.get().getTipo());
			usuarioLogin.get().setCep(usuario.get().getCep());
			usuarioLogin.get().setBairro(usuario.get().getBairro());
			usuarioLogin.get().setCidade(usuario.get().getCidade());
			usuarioLogin.get().setEstado(usuario.get().getEstado());
			usuarioLogin.get().setPais(usuario.get().getPais());
			usuarioLogin.get().setRua(usuario.get().getRua());
			usuarioLogin.get().setComplemento(usuario.get().getComplemento());
			return usuarioLogin;
			
			}
		}
		
		return Optional.empty();
	}
	
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);
	}
	
	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.matches(senhaDigitada, senhaBanco);
	}
	
	private String gerarBasicToken(String usuario, String senha) {
		
		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		 return "Basic " + new String(tokenBase64);
		}
	
	}
