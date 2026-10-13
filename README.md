# Edufy — User Service

Part of Edufy, a 10+ service microservices streaming platform (see [gateway repo](link) for the system overview). This service owns user data and identity.

**What I built:** full CRUD and endpoints for user management, including a `getUserByUsername` lookup endpoint, DTO design (including resolving a naming conflict between a Keycloak-facing DTO and the internal user DTO), a gateway client for inter-service calls, and the Keycloak security integration for this service specifically (config and converters).

**Stack:** Java, Spring Boot, Spring Security, Keycloak (OAuth2 resource server), Docker.
