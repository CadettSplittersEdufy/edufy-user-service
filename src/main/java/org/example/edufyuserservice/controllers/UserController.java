package org.example.edufyuserservice.controllers;

import org.example.edufyuserservice.DTOs.KeycloakDTO;
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
    public ResponseEntity<KeycloakDTO> addUser(@RequestBody KeycloakDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.newUser(userDTO));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<KeycloakDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<KeycloakDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/updateuser")
    public ResponseEntity<KeycloakDTO> updateUser(@RequestBody KeycloakDTO user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PutMapping("/inactivateuser/{id}")
    public ResponseEntity<String> setUserInactive(@PathVariable Long id) {
        userService.setUserInactive(id);
        return ResponseEntity.ok("User: " + userService.getUserById(id).getUsername() + " set to inactive");
    }


    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with id: " + id + " deleted");
    }
}
