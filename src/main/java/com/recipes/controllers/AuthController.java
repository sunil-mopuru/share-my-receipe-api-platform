package com.recipes.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipes.entities.Chef;
import com.recipes.entities.Recipe;
import com.recipes.payload.request.LoginRequest;
import com.recipes.payload.request.SignupRequest;
import com.recipes.payload.response.JwtResponse;
import com.recipes.payload.response.MessageResponse;
import com.recipes.repositories.ChefRepository;
import com.recipes.security.JwtUtils;
import com.recipes.security.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ChefRepository chefRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateChef(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId().toString(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerChef(@Valid @RequestBody SignupRequest signUpRequest) {
        if (chefRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        if (chefRepository.existsByHandle(signUpRequest.getHandle())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Handle is already taken!"));
        }

        // Create new chef's account
        Chef chef = new Chef(signUpRequest.getHandle(),
                signUpRequest.getHandle(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        chefRepository.save(chef);

        return ResponseEntity.ok(new MessageResponse("Chef registered successfully!"));
    }
}