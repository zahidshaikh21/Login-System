package com.example.jobPortalApp.controller;

import com.example.jobPortalApp.models.AuthenticationRequest;
import com.example.jobPortalApp.models.AuthenticationResponse;
import com.example.jobPortalApp.models.UserDTO;
import com.example.jobPortalApp.services.MyUserDetailService;
import com.example.jobPortalApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class Home {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtUtil jwtTokenUtil;


    @GetMapping("/")
    public String home() {
        return ("Welcome you are an authorized user");
    }


    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(myUserDetailService.save(user));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToke(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect Username or Password", e);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User or Password incorrect", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }

}
