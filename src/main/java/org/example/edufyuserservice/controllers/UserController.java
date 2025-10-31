package org.example.edufyuserservice.controllers;

import org.example.edufyuserservice.DTOs.UserDTO;
import org.example.edufyuserservice.entities.User;
import org.example.edufyuserservice.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "This is the user controller test";
    }

    @PostMapping("/newuser")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.newUser(userDTO));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/updateuser")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PutMapping("/inactivateuser")
    public ResponseEntity<String> setUserInactive(@PathVariable Long id) {
        userService.setUserInactive(id);
        return ResponseEntity.ok("User set to inactive");
    }


    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
