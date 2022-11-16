package com.Omer.jwt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@SpringBootApplication
public class JwtApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(JwtApplication.class, args);
	}
/*
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}


*/



	@Override
	public void run(String... args) throws Exception // program ayağı kalkareken çalışacak olan metod
	{
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encoded = bCryptPasswordEncoder.encode("123");
		System.out.println(encoded);
	}
}


