package com.Omer.jwt.controller;

import com.Omer.jwt.entity.AuthorizeReq;
import com.Omer.jwt.service.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomePageController {

	@Autowired
	private JwtUtilService jwtUtilService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@GetMapping("/")
	public String welcomePage() {
		return "Success to load welcome page...!";
	}
	
	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthorizeReq authorizeReq) throws Exception {
		
		try {
			System.out.println("Action in controller authenticat....");
			authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(authorizeReq.getUsername(), authorizeReq.getPassword())
					);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Invalid username/password");
		}
		
		return jwtUtilService.generateToken(authorizeReq.getUsername());
		
	}
	
}
