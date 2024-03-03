package com.example.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private RestTemplate restTemplate;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(validator.isSecure.test(exchange.getRequest())){
                System.out.println(exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("missing authorization header");
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                    System.out.println(authHeader);
                }
                try{
                    restTemplate.getForObject("http://localhost:8081/api/v1/auth/validate", String.class);
                } catch (Exception e){
                    System.out.println("invalid access");
                    throw new RuntimeException("unauthorized access to application");
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config{

    }
}
