package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//Fetch user from Database
		User user = userRepository.getUserByUserName(username);
		 if(user==null) {
			 throw new UsernameNotFoundException("User not Found!!");
		 }
		 
		 CustomUserDetails customUserDetails = new CustomUserDetails(user);
		 return customUserDetails;
	}

}
