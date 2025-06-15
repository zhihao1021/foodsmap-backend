package com.nckueat.foodsmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.nckueat.foodsmap.repository.postgresql")
@EnableElasticsearchRepositories(basePackages = "com.nckueat.foodsmap.repository.elasticsearch")
public class FoodsmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodsmapApplication.class, args);
	}
}
