package com.smartcontact.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.smartcontact.entities.User;

@Controller
public class TestController {

	@GetMapping("/test")
	@Transactional
	public String Test(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		System.out.println("user found " +user);
		return "test";
	}
}
