package com.smartcontact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;

@Controller
public class HomeController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String home(Model model,HttpSession session) {
		
		User user = (User)session.getAttribute("user");
		if(user!=null)
		{
			return "redirect:/user/index";
		}
		
		model.addAttribute("title","Home-Smart contact manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","About-Smart contact manager");
		return "about";
	}
	
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register-Smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	
	//Registering User
	@RequestMapping(value = "/do_register",method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement ,Model model,HttpSession session ) 
	{
		
		try {
			
			if(!agreement) 
			{
				throw new Exception("You have not agreed to terms and license agreement!!");
			}
			
			if(result.hasErrors()) 
			{
				model.addAttribute("user",user);	//Sending same data back to user and will be populated in the fields ,even though error occured so that user do not have to fill form data from scratch
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(user);
			User resultUser = this.userRepository.save(user);
			
			System.out.println(resultUser);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));		//Sending alert msg to signup using session --> alert message shown is conditional
			return "signup";
			
			
		}catch (Exception e) {
			
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(), "alert-danger"));
			return "signup";
			
		}
		
	}

	
	//login
	@RequestMapping("/signin")
	public String customlogin(Model model,HttpSession session) {
		
		User user = (User)session.getAttribute("user");
		if(user!=null)
		{
			return "redirect:/user/index";
		}
		
		model.addAttribute("title","Login-Smart contact manager");
		return "login";
	}
}
