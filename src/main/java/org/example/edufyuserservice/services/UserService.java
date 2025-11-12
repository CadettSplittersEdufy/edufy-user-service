package org.example.edufyuserservice.services;

import org.example.edufyuserservice.DTOs.KeycloakDTO;

import java.util.List;

public interface UserService {

    KeycloakDTO newUser(KeycloakDTO userDTO);

    List<KeycloakDTO> getAllUsers();

    KeycloakDTO getUserById(Long id);

    KeycloakDTO updateUser(KeycloakDTO userDTO);

    void setUserInactive(Long id);

    void deleteUser(Long id);
}
