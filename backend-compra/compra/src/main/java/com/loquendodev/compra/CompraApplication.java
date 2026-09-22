package com.loquendodev.compra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CompraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompraApplication.class, args);
	}



	//REST Template para poder obtener los atributos de los otros microservicios:

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
