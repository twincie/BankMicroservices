package com.example.gateway.filter;

import com.example.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = null;
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
//                Integer intUserId = jwtUtil.extractUserId(authHeader);
//                String userId = intUserId.toString();
//                Integer intWalletId = jwtUtil.extractWalletId(authHeader);
//                String walletId = intWalletId.toString();
                String role = jwtUtil.extractRole(authHeader);
                System.out.println(role);
                try{
                    //restTemplate.getForObject("http://localhost:8081/api/v1/auth/validate", String.class);
                    jwtUtil.validateToken(authHeader);
                    request= exchange.getRequest()
                            .mutate()
                            .header("loggedInUser", jwtUtil.extractUserName(authHeader))
                            .header("loggedInUserId", jwtUtil.extractUserId(authHeader).toString())
                            .header("loggedInWalletId", jwtUtil.extractWalletId(authHeader).toString())
                            .header("role", jwtUtil.extractRole(authHeader))
                            .build();

                } catch (Exception e){
                    System.out.println("invalid access");
                    throw new RuntimeException("unauthorized access to application");
                }
            }
//            System.out.println(exchange.mutate().request(request).build());
            return chain.filter(exchange.mutate().request(request).build());
        };
    }



    public static class Config{

    }
}
