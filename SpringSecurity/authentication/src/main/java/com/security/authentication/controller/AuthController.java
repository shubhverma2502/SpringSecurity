package com.security.authentication.controller;

import com.security.authentication.model.User;
import com.security.authentication.payload.LoginResponsePayload;
import com.security.authentication.repository.RoleRepository;
import com.security.authentication.repository.UserRepository;
import com.security.authentication.security.jwt.JwtUtils;
import com.security.authentication.service.UserDetailsImpl;
import com.security.common.enums.ERole;
import com.security.common.service.MailService;
import com.security.common.service.OtpService;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpService otpService;

    @Autowired
    MailService mailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;


    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody User userPayload) {

        var username = getOptionalUser(userPayload.getUsername());
        if (username.isPresent()) {
            LOG.debug("An account with email {} already exists.", userPayload.getUsername());
            return new ResponseEntity<>("User with given email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setId(userPayload.getId());
        user.setUsername(userPayload.getUsername());
        user.setFirstname(userPayload.getFirstname());
        user.setLastname(userPayload.getLastname());
        user.setPassword(passwordEncoder.encode(userPayload.getPassword()));

        this.roleRepository.findByName(ERole.ROLE_USER.getValue())
                .ifPresent(role -> user.setRoles(Set.of(role)));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        var username = getOptionalUser(user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new LoginResponsePayload(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFirstname(), userDetails.getLastname(), roles));
        } catch (AuthenticationException e) {
            LOG.error("Authentication failed ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");

        }
    }

    @PostMapping("/auth/generate/otp")
    public ResponseEntity<?> generateOTP(@RequestBody User userPayload, @PathParam("channel") String channel) {
        final String email = userPayload.getUsername();
        Optional<User> optionalUser = userRepository.findByUsername(userPayload.getUsername());
        if (userPayload.isValidate()) {
            if (optionalUser.isPresent()) {
                LOG.debug("An account with email {} already exists.", userPayload.getUsername());
                return new ResponseEntity<>("User with given email already exists.", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (optionalUser.isEmpty()) {
                LOG.debug("An account with email {} doesn't exists.", userPayload.getUsername());
                return new ResponseEntity<>("User with given email doesn't exists", HttpStatus.BAD_REQUEST);
            }
        }
        if (!this.generateOTP(channel,email)) {
            return new ResponseEntity<>("An error occurred while processing the OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("OTP has been sent successfully.",HttpStatus.OK);
}

    private boolean generateOTP(String channel,String username) {
        var template = "otp-template.ftl";
        var subject = Optional.of("Verification code");
        if (channel.equals("email")) {
            otpService.generateOTP(username);
        }
        return mailService.sendMail(username, otpService.getOtp(username), template, subject, username);

    }

    @PostMapping("/auth/reset")
    public ResponseEntity<?> resetUser(@RequestBody User userPayload){
            var user = getOptionalUser(userPayload.getUsername());
            if(user.isEmpty()){
                LOG.debug("An account with email {} doesn't exists.", userPayload.getUsername());
                return new ResponseEntity<>("User with given email doesn't exists", HttpStatus.BAD_REQUEST);
            }
            return saveUser(user.get(),userPayload,"reset");
    }

    private ResponseEntity<?> saveUser(final User user,final User userPayload,final String type){
        if(userPayload.isPasswordAndConfirmPasswordMatches()){
            if(validateOTP(userPayload.getOtp(), userPayload.getUsername())){
                if(type.equals("reset")){
                    user.setPassword(passwordEncoder.encode(userPayload.getPassword()));
                    userRepository.save(user);
                    otpService.clearOTP(userPayload.getUsername());
                    return ResponseEntity.status(HttpStatus.CREATED).body("User Password Updated Successfully");
                }
                otpService.clearOTP(userPayload.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password did not match");
    }

    private boolean validateOTP(final int otp,final String username){
        return (otp == otpService.getOtp(username));
    }

    private Optional<User> getOptionalUser(String username) {
        return userRepository.findByUsername(username);
    }


}
