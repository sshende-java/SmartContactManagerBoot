package com.smartcontact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	//pagination
	// needs 'Current page' and 'Contacts per page'
	
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	public List<Contact> findByPhoneAndUserId(String phone,int uid);
	
	public Contact findBycIdAndUserId(int cid,int uid);
	
	//method for searching contact
	public List<Contact> findByNameContainingAndUser(String name,User user);  		/* where column like '%urabh%' */
	
	
}
