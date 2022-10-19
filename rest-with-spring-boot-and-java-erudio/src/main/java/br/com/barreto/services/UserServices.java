package br.com.barreto.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.barreto.repositories.UserRepository;

@Service
public class UserServices implements UserDetailsService {

	
	private Logger logger = Logger.getLogger(UserServices.class.getName());
	
	@Autowired
	UserRepository repository;

		public UserServices(UserRepository repository) {
		this.repository = repository;
	}

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			// TODO Auto-generated method stub
			logger.info("find one use by name "+username+"!");
			var user = repository.findByUsername(username);
			if (user != null) {
				return user;
			} else {
					throw new UsernameNotFoundException("Username "+username+" Not Founf!");
			}
		}	
}
