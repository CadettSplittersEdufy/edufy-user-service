package org.example.edufyuserservice.clients;

import org.example.edufyuserservice.DTOs.KeycloakDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GatewayClient {

    private RestClient restClient;

    public GatewayClient(RestClient.Builder restClient) {
        String gatewayUrl = "http://edufy-gateway-service:4505/keycloak";
        this.restClient = restClient
                .baseUrl(gatewayUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String createKeycloakId(KeycloakDTO userDTO) {
        System.out.println("fetching keycloak id from gateway-service via keycloak client");

        String gatewayServiceResponse = restClient.post()
                .uri("/newuser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO)
                .retrieve()
                .body(String.class);

        return gatewayServiceResponse + " from gateway service via keycloak client";
    }
}
