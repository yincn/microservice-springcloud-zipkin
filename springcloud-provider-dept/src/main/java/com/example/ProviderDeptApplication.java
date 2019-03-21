package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@EnableCircuitBreaker
@EnableHystrix
@SpringBootApplication
public class ProviderDeptApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderDeptApplication.class, args);
	}

}
