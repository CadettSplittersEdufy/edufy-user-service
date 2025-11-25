package org.example.edufyuserservice.clients;

import org.example.edufyuserservice.DTOs.UserDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GatewayClient {

    private final RestClient restClient;


    public GatewayClient(RestClient.Builder restClient){

        String gatewayUrl = "http://edufy-gateway-service:4505/keycloak";
        this.restClient = restClient
                .baseUrl(gatewayUrl)
                .requestInterceptor((request, body, execution) -> {

                    var auth = SecurityContextHolder.getContext().getAuthentication();

                    if (auth instanceof JwtAuthenticationToken jwtAuth) {
                        String token = jwtAuth.getToken().getTokenValue();
                        request.getHeaders().setBearerAuth(token);
                    }

                    return execution.execute(request, body);
                })
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    public String createKeycloakId(UserDTO userDTO) {
        System.out.println("fetching keycloak id from gateway-service via keycloak client");


        String gatewayServiceResponse = restClient.post()
                .uri("/newuser")
                .body(userDTO)
                .retrieve()
                .body(String.class);

        return gatewayServiceResponse + " from gateway service via keycloak client";
    }

    public String fetchKeycloakId(String username) {
        System.out.println("fetching keycloak id from gateway-service");

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/userid/{username}")
                        .queryParam("username", username)
                        .build(username))
                .retrieve()
                .body(String.class);
    }

    public List<String> fetchUserRoles(String username) {
        System.out.println("fetching user roles from gateway-service");
            ResponseEntity<List<String>> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getuserrole/{username}")
                            .queryParam("username", username)
                            .build(username))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<String>>() {});

            return response.getBody();
    }

    public boolean keycloakUserExists(String username){

        return Boolean.TRUE.equals(restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usernameexists/{username}")
                        .queryParam("username", username)
                        .build(username))
                .retrieve()
                .body(Boolean.class));

    }
}
