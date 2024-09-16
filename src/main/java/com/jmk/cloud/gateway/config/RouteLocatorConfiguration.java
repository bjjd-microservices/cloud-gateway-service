package com.jmk.cloud.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@SuppressWarnings("deprecation")
@Configuration
public class RouteLocatorConfiguration {
	
	@Resource(name="requestHeaderFilter")
	private GatewayFilter gatewayFilter;
	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;
	
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/api/user-service/**")
				.filters(f -> f.filter(filterFactory.apply())
						.removeRequestHeader("Cookie"))
				.uri("lb://user-service") //.uri("http://127.0.0.1:3379")
				)

				.route(r -> r.path("/api/project-service/**")
						.filters(f -> f.filter(filterFactory.apply())
								.removeRequestHeader("Cookie"))
						.uri("lb://project-service/")
						)
				
				.route(r -> r.path("/api/people-service/**")
						.filters(f -> f.filter(gatewayFilter))
						.uri("lb://people-service/")
						)
				
				.route(r -> r.path("/api/darshan-service/**")
						.filters(f -> f.filter(gatewayFilter))
						.uri("lb://darshan-service/")
						)
				
				.route(r -> r.path("/api/account-service/**")
						.filters(f -> f.filter(gatewayFilter))
						.uri("lb://account-service/")
						)
				
				.route(r -> r.path("/api/data-upload-service/**")
						.filters(f -> f.filter(gatewayFilter))
						.uri("lb://data-upload-service/")
						)
		
				.build();
	}
		
}

