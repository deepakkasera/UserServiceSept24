package com.example.userservicedec24.services;

import com.example.userservicedec24.exceptions.UnAuthorizedException;
import com.example.userservicedec24.exceptions.UserNotFoundException;
import com.example.userservicedec24.models.Token;
import com.example.userservicedec24.models.User;

public interface UserService {
    User signUp(String name, String email, String password);

    Token login(String email, String password) throws UserNotFoundException, UnAuthorizedException;

    User validateToken(String tokenValue);

    void logout(String tokenValue);
}
