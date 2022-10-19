package br.com.barreto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.barreto.data.vo.v1.security.AccountCredentialVO;
import br.com.barreto.data.vo.v1.security.TokenVO;
import br.com.barreto.repositories.UserRepository;
import br.com.barreto.security.Jwt.JwrTokenProvider;

@Service
public class AuthServices {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private JwrTokenProvider tokenProvider;
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialVO data) {
		try {
			var username =  data.getUsername();
			var passworord =  data.getPassword();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, passworord));
			var user = repository.findByUsername(username);
			var tokenResponse = new TokenVO();
			
			if (user != null) {
				tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
			} else {
				throw new UsernameNotFoundException("Username "+ username+ " not found");
			} 
			
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid username/password supplied!");
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {

			var user = repository.findByUsername(username);
			var tokenResponse = new TokenVO();
			
			if (user != null) {
				tokenResponse = tokenProvider.refreshToken(refreshToken);
			} else {
				throw new UsernameNotFoundException("Username "+ username+ " not found");
			} 
			
			return ResponseEntity.ok(tokenResponse);
	}
}
