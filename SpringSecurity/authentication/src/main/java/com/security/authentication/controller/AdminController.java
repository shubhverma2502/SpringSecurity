package com.security.authentication.controller;

import com.security.authentication.model.User;
import com.security.authentication.repository.UserRepository;
import com.security.common.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin")
    public ResponseEntity<?> admin(){
        return ResponseEntity.ok("Hiii Admin");
    }
    @GetMapping("/admin/getUser/{id}")
    public ResponseEntity getUserById(@PathVariable("id") Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        return ResponseEntity.ok(optionalUser);

    }


    public User updateRole(Long id, Set<Role> roles){
        Optional<User> byId = userRepository.findById(id);
        if(byId.isPresent()){
            var user = byId.get();
            user.setRoles(roles);
            return userRepository.save(user);
        }
        return null;
    }

    @PostMapping("/admin/update")
    public ResponseEntity<?> updateUserRole(@RequestBody User user){
        User updatedUser = updateRole(user.getId(),user.getRoles());
        return ResponseEntity.ok(updatedUser.getRoles().equals(user.getRoles())? "User role updated successfully" : "Unable to update user role");
    }
}
