package org.example.edufyuserservice.services;

import org.example.edufyuserservice.DTOs.UserDTO;
import org.example.edufyuserservice.entities.User;
import org.example.edufyuserservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO newUser(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setActive(true);
        userRepository.save(newUser);
        return convertToDTO(newUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return convertToDTOList(users);
    }

    @Override
    public UserDTO getUserById(Long id) {
        if(userRepository.findById(id).isPresent()) {
        return convertToDTO(userRepository.findById(id).get());
    }else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    }

    @Override
    public UserDTO updateUser(UserDTO userToUpdate) {
        if(userToUpdate.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User ID is required");
        }

        Optional<User> userOptional = userRepository.findById(userToUpdate.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userToUpdate.getUsername());
            user.setFirstName(userToUpdate.getFirstName());
            user.setLastName(userToUpdate.getLastName());
            user.setActive(userToUpdate.isActive());
            userRepository.save(user);
            return convertToDTO(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userToUpdate.getId() + " not found");
        }
    }

    @Override
    public void setUserInactive(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }

    }

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setActive(user.isActive());
        return userDTO;
    }

    public List<UserDTO> convertToDTOList(List<User> users){
        if (users == null || users.isEmpty()) {
            UserDTO emptyMessageDTO = new UserDTO();
            emptyMessageDTO.setMessage("No users found");
            return List.of(emptyMessageDTO);
        }

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }
    }
}
