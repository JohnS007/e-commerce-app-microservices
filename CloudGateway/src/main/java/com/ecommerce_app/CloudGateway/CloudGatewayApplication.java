package com.ecommerce_app.CloudGateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGatewayApplication.class, args);
	}

	//Adding bean to configure the default configuration of Resilience4J
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> resilience4JCircuitBreakerFactoryCustomizer(){
		return factory -> factory.configureDefault(
				id -> new Resilience4JConfigBuilder(id).circuitBreakerConfig(
						CircuitBreakerConfig.ofDefaults()
				).build()
		);
	}

	//Adding KeyResolver to store the config information for users
	@Bean
	public KeyResolver keyResolver(){
		return exchange -> Mono.just("userKey");
	}

}
