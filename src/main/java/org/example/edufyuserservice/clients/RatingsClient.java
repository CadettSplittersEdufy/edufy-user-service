package org.example.edufyuserservice.clients;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RatingsClient {

    private final RestClient restClient;

    public RatingsClient(RestClient.Builder restClient) {
        String ratingsUrl = "http://edufy-ratings-service:4585";
        this.restClient = restClient
                .baseUrl(ratingsUrl)
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

    public String callRatingsService() {

            return restClient.get()
                    .uri("/ratings/test")
                    .retrieve()
                    .body(String.class);
        }



}
