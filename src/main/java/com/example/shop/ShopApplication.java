package com.example.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
public class ShopApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ShopApplication.class)
				.beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
				.run(args);
	}
}
