package com.example.userservicedec24.controllers;

import com.example.userservicedec24.dtos.LoginRequestDto;
import com.example.userservicedec24.dtos.LogoutRequestDto;
import com.example.userservicedec24.dtos.SignUpRequestDto;
import com.example.userservicedec24.dtos.UserDto;
import com.example.userservicedec24.exceptions.UnAuthorizedException;
import com.example.userservicedec24.exceptions.UserNotFoundException;
import com.example.userservicedec24.models.Token;
import com.example.userservicedec24.models.User;
import com.example.userservicedec24.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// http://localhost:8080/users/login
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto requestDto) throws UserNotFoundException, UnAuthorizedException {
        return userService.login(
                requestDto.getEmail(),
                requestDto.getPassword()
        );
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto requestDto) {
        User user = userService.signUp(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        //Convert this User object into UserDto object.

        return UserDto.from(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestBody LogoutRequestDto requestDto) {
        userService.logout(requestDto.getTokenValue());
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @GetMapping("/validate")
    public UserDto validateToken(@PathVariable String tokenValue) {
        return null;
    }
}
