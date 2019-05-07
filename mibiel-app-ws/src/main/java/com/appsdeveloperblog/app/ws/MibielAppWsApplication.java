package com.appsdeveloperblog.app.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
//@PropertySource("classpath:application.properties")

public class MibielAppWsApplication{

	public static void main(String[] args) {
		SpringApplication.run(MibielAppWsApplication.class, args);
		
		
	}
	
//	@Value( "${username}" )
//	private String asb;
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		//System.out.println(asb);
		return new BCryptPasswordEncoder();
	}

}

