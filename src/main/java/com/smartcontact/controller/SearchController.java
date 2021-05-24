package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.dao.ContactRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//search handler for contacts by name
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		String userName = principal.getName();		//This will retrieve email
		User user = userRepository.getUserByUserName(userName);
		
		List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
	
}
