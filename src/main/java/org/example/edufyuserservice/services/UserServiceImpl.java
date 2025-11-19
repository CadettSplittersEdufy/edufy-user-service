package org.example.edufyuserservice.services;

import org.example.edufyuserservice.DTOs.KeycloakDTO;
import org.example.edufyuserservice.clients.GatewayClient;
import org.example.edufyuserservice.entities.User;
import org.example.edufyuserservice.exceptions.UserNotFoundException;
import org.example.edufyuserservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GatewayClient gatewayClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, GatewayClient gatewayClient) {
        this.userRepository = userRepository;
        this.gatewayClient = gatewayClient;
    }


    @Override
    public KeycloakDTO newUser(KeycloakDTO user) {
        System.out.println("Creating a new user method called");
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setActive(true);
        try {
            newUser.setKeycloakId(gatewayClient.createKeycloakId(user));
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("Could not fetch Keycloak ID for user: " + newUser.getUsername(), e);
        }

        userRepository.save(newUser);
        return convertToDTO(newUser);
    }


    @Override
    public List<KeycloakDTO> getAllUsers() {
        System.out.println("Retrieving all users method called");
        List<User> users = userRepository.findAll();
        return convertToDTOList(users);
    }

    @Override
    public KeycloakDTO getUserById(Long id) {
        System.out.println("Retrieving user by id method called");
        if(userRepository.findById(id).isPresent()) {
        return convertToDTO(userRepository.findById(id).get());
    }else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    }

    @Override
    public KeycloakDTO updateUser(KeycloakDTO userToUpdate) {
        System.out.println("Updating user method called");
        if(userToUpdate.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User ID is required");
        }

        Optional<User> userOptional = userRepository.findById(userToUpdate.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userToUpdate.getUsername());
            user.setFirstName(userToUpdate.getFirstName());
            user.setLastName(userToUpdate.getLastName());
            user.setEmail(userToUpdate.getEmail());
            user.setActive(userToUpdate.isActive());
            userRepository.save(user);
            return convertToDTO(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userToUpdate.getId() + " not found");
        }
    }

    @Override
    public void setUserInactive(Long id) {
        System.out.println("Updating user inactive method called");
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }

    }

    public KeycloakDTO convertToDTO(User user) {
        System.out.println("Converting User to KeycloakDTO method called");
        KeycloakDTO userDTO = new KeycloakDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.isActive());

        String keycloakId = user.getKeycloakId();
        if(keycloakId == null || keycloakId.isEmpty()){
            String username = user.getUsername();
            if(gatewayClient.keycloakUserExists(username))
                userDTO.setKeycloakId(gatewayClient.fetchKeycloakId(username));
        } else {
           userDTO.setKeycloakId(keycloakId);
        }

        return userDTO;
    }

    public List<KeycloakDTO> convertToDTOList(List<User> users){
        System.out.println("Converting Users list to KeycloakDTO list method called");
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("Deleting user by id method called");
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }
    }
}
