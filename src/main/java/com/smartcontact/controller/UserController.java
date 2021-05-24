package com.smartcontact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.dao.ContactRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private ContactRepository contactRepository;
	
	//Dashboard
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal,HttpSession session)
	{
		String userName = principal.getName();		//This will retrieve email
		model.addAttribute("title","Dashboard-Smart contact manager");
		User user = userRepository.getUserByUserName(userName);
		
		
		session.setAttribute("user", user);
		
		return "normal/user_dashboard";
	} 

	
	//Open Add Contact form
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model) {
		

		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	}
	
	
	//Process add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,BindingResult result,
								 @RequestParam("profileImage") MultipartFile file,
								 Principal principal,Model model,HttpSession session)
	{
		
		try {
			
			if(result.hasErrors()) 
			{
				model.addAttribute("contact",contact);
				return "normal/add_contact_form";
			}
			
			
			String name = principal.getName();		//Will fetch email
			User user = userRepository.getUserByUserName(name);
			
			//check if contact already exists!!
			List<Contact> contactlist = contactRepository.findByPhoneAndUserId(contact.getPhone(), user.getId());
			if(!contactlist.isEmpty()) {
				
				session.setAttribute("message",new Message("Contact Already Exists!!", "alert-danger"));
				return "normal/add_contact_form";
			}
			
			if(file.isEmpty()) {
				
				//file is empty
				//by default imageUrl will be set to contact.png
				contact.setImageUrl("contact.png");
				
			}else {
				
				//upload file to folder static/img
				contact.setImageUrl(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);		/* Files.copy(inputStream , target path , options)	*/
				
							
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);			//list.add
			
			this.userRepository.save(user);
			
			//message success.......
			session.setAttribute("message",new Message("Contact Added Successfully!!", "alert-success"));
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			//message failure.....
			session.setAttribute("message",new Message("Something went wrong..try again !!", "alert-danger"));
			
		}
		
		return "normal/add_contact_form";
	}

	
	
	
	//show contacts handler includes pagingation
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		
		model.addAttribute("title","Show Contact");			//title will be sent to base.html of normal/base.html
		
		//get user whose contacts to be shown
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		
		//1.current-page		-->page
		//2.contacts per page	--> Pageable needs these two
		Pageable pageable = PageRequest.of(page, 5);
		
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		
		return "normal/show_contacts";
		
	}
	
	
	
	//Access single contact
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model,Principal principal,HttpSession session)
	{
		model.addAttribute("title","Contact Details");	
		
		//get user whose contacts to be shown
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		Contact contact = contactRepository.findBycIdAndUserId(cId, user.getId());
		//check if contact is null --> if user tries to modify url then he will be redirected to dashboard with error
		if(contact == null) {
			session.setAttribute("message",new Message("No Such Contact Exists !!", "alert-danger"));
			return "normal/user_dashboard";
		}
		
		model.addAttribute("contact",contact);
		
		return "normal/contact_detail";
	}
	
	
	
	//Delete contact
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Principal principal,HttpSession session)
	{
		//get user 
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		Contact contact = contactRepository.findBycIdAndUserId(cId, user.getId());
		if(contact == null) {
			session.setAttribute("message",new Message("No Such Contact Exists !!", "alert-danger"));
			return "normal/user_dashboard";
		}
		
		contact.setUser(null);		//You have to unlink userId from contact table as we are maintaining cascadeType.ALL
		
		//delete associated image from system too !!
		
		
		
		contactRepository.delete(contact);
		session.setAttribute("message",new Message("Contact Deleted Successfully !!", "alert-success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	
	
	//Open Update Contact form
	@GetMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,Model model,Principal principal,HttpSession session)
	{
		
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		Contact contact = contactRepository.findBycIdAndUserId(cId, user.getId());
		//check if contact is null --> if user tries to modify url then he will be redirected to dashboard with error
		if(contact == null) {
			session.setAttribute("message",new Message("No Such Contact Exists !!", "alert-danger"));
			return "normal/user_dashboard";
		}

		model.addAttribute("title","Update-Contact");
		model.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	
	
	
	//Process Update contact
	@PostMapping("/process-update")
	public String updateContactHandler(@Valid @ModelAttribute Contact contact,BindingResult result,
			 @RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal)
	{
		
		try {
			
			//old contact details
			Contact oldContact = contactRepository.findById(contact.getcId()).get();
			
			
			
			if(result.hasErrors()) 
			{
				model.addAttribute("contact",contact);
				return "normal/update_form";
			}
			
			if(!file.isEmpty()) {
				//file work
			
				//delete old photo rewrite
				if(oldContact.getImageUrl() != "contact.png")			//we dont want to delete default img i.e contact.png
				{
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile, oldContact.getImageUrl());
					file1.delete();
				}
				
				
				
				//upload new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+ File.separator + file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);		/* Files.copy(inputStream , target path , options)	*/
				
				contact.setImageUrl(file.getOriginalFilename());
				
				
			}else {
				//if file is not chosen by user then set same old image
				contact.setImageUrl(oldContact.getImageUrl());
			}
			
			String name = principal.getName();		//Will fetch email
			User user = userRepository.getUserByUserName(name);
			
			contact.setUser(user);
			contactRepository.save(contact);
			
			session.setAttribute("message",new Message("Contact Updated !!", "alert-success"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong..try again !!", "alert-danger"));
		}
			
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	
	
	//Profile handler
	@GetMapping("/profile")
	public String profileHandler(Model model,Principal principal) {
		
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		model.addAttribute("user",user);
		model.addAttribute("title","Profile- Smart Contact Manager");
		return "normal/profile";
	}
	
	
	//Settings handler
	@GetMapping("/settings")
	public String openSettings(Model model)
	{
		model.addAttribute("title","Settings- Smart Contact Manager");
		return "normal/settings";
	}
	
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session)
	{
		String name = principal.getName();					//Will fetch email
		User user = userRepository.getUserByUserName(name);
		
		if(bcrypt.matches(oldPassword, user.getPassword()))
		{
			//change password
			user.setPassword(bcrypt.encode(newPassword));
			userRepository.save(user);
			
			session.setAttribute("message",new Message("Password changed successfully !!", "alert-success"));
		}
		else
		{
			session.setAttribute("message",new Message("Incorrect old password !!", "alert-danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
	
}








