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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Component
public class GatewayClient {

    private final RestClient restClient;

    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);



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

    public void setUserRole(UserDTO userDTO){
        try {
            restClient.post()
                    .uri("/assignrole")
                    .body(userDTO)
                    .retrieve()
                    .body(void.class);
        } catch (Exception e) {
            log.error("Failed to assign role for user {}", userDTO.getUsername(), e);
            throw e;
        }
    }


    public String createKeycloakId(UserDTO userDTO) {
            try{

        String gatewayServiceResponse = restClient.post()
                .uri("/newuser")
                .body(userDTO)
                .retrieve()
                .body(String.class);

        return gatewayServiceResponse + " from gateway service via keycloak client";
            } catch (Exception e) {
                log.error("Failed to create Keycloak ID for username {}", userDTO.getUsername(), e);
                throw e;
            }
    }


    public String fetchKeycloakId(String username) {
        try{
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/userid/{username}")
                        .queryParam("username", username)
                        .build(username))
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            log.error("Failed to fetch Keycloak ID for username {}", username, e);
            throw e;
        }
    }

    public List<String> fetchUserRoles(String username) {
        try{
            ResponseEntity<List<String>> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getuserrole/{username}")
                            .queryParam("username", username)
                            .build(username))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<String>>() {});

            return response.getBody();
            } catch (Exception e) {
                     log.error("Failed to fetch roles for username {}", username, e);
                        throw e;
                        }
    }

    public boolean keycloakUserExists(String username){

        try{
            return Boolean.TRUE.equals(restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usernameexists/{username}")
                        .queryParam("username", username)
                        .build(username))
                .retrieve()
                .body(Boolean.class));

         } catch (Exception e) {
                log.error("Failed to check if Keycloak user exists: {}", username, e);
                throw e;
            }
        }
}
