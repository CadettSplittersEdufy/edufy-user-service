package org.example.edufyuserservice.services;

import org.example.edufyuserservice.DTOs.UserDTO;
import org.example.edufyuserservice.entities.User;

import java.util.List;

public interface UserService {

    UserDTO newUser(UserDTO userDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUser(UserDTO userDTO);

    void setUserInactive(Long id);

    void deleteUser(Long id);
}
