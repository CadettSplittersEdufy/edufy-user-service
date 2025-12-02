package org.example.edufyuserservice.services;

import org.example.edufyuserservice.DTOs.UserDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final GatewayClient gatewayClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, GatewayClient gatewayClient) {
        this.userRepository = userRepository;
        this.gatewayClient = gatewayClient;
    }


    @Override
    public UserDTO newUser(UserDTO user) {
        System.out.println("Creating a new user method called");
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setActive(true);

        if (user.getRole() == null) {
            newUser.setRole("edufy_USER");
        }
        try {
            newUser.setKeycloakId(gatewayClient.createKeycloakId(user));
        } catch (UserNotFoundException e) {
            log.error("Failed to create Keycloak ID for user {}", newUser.getUsername(), e);
            throw new IllegalArgumentException("Could not fetch Keycloak ID for user: " + newUser.getUsername(), e);
        }

        userRepository.save(newUser);
        UserDTO newUserDTO = convertToDTO(newUser);
        try {
            gatewayClient.setUserRole(newUserDTO);
        } catch (Exception e) {
            log.error("Failed to assign role for user {}", newUser.getUsername(), e);
            throw e;
        }

        return newUserDTO;
    }


    @Override
    public List<UserDTO> getAllUsers() {
        System.out.println("Retrieving all users method called");
        List<User> users = userRepository.findAll();
        return convertToDTOList(users);
    }

    @Override
    public UserDTO getUserById(Long id) {
        System.out.println("Retrieving user by id method called");
        if(userRepository.findById(id).isPresent()) {
        return convertToDTO(userRepository.findById(id).get());
    }else {
            log.error("User with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    }

    @Override
    public UserDTO updateUser(UserDTO userToUpdate) {
        System.out.println("Updating user method called");
        if(userToUpdate.getId() == null) {
            log.error("User update attempted with null ID");
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
            log.error("User with id {} not found for update", userToUpdate.getId());
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
            log.error("User with id {} not found to set inactive", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }

    }

    public UserDTO convertToDTO(User user) {
        System.out.println("Converting User to UserDTO method called");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.isActive());

        String role = user.getRole();
        if(role == null || role.isEmpty()) {
            String username = user.getUsername();
            try {
                if (gatewayClient.keycloakUserExists(username)) {
                    List<String> newRole = gatewayClient.fetchUserRoles(username);
                    userDTO.setRole(newRole.getFirst());
                }
            } catch (Exception e) {
                log.error("Failed to fetch roles for {}", username, e);
            }
        } else {
            userDTO.setRole(role);
        }

        String keycloakId = user.getKeycloakId();
        if (keycloakId == null || keycloakId.isEmpty()) {
            String username = user.getUsername();
            try {
                if (gatewayClient.keycloakUserExists(username)) {
                    userDTO.setKeycloakId(gatewayClient.fetchKeycloakId(username));
                }
            } catch (Exception e) {
                log.error("Failed to fetch Keycloak ID for {}", username, e);
            }
        } else {
            userDTO.setKeycloakId(keycloakId);
        }

        return userDTO;
    }


    public List<UserDTO> convertToDTOList(List<User> users){
        System.out.println("Converting Users list to UserDTO list method called");
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<String> getUserRoles(String username){
        try {
            return gatewayClient.fetchUserRoles(username);
        } catch (Exception e) {
            log.error("Failed to fetch roles for {}", username, e);
            throw e;
        }
    }


    @Override
    public void deleteUser(Long id) {
        System.out.println("Deleting user by id method called");
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            log.error("User with id {} not found for deletion", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }
    }
}
