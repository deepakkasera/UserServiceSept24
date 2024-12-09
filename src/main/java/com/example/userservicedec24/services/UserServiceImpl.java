package com.example.userservicedec24.services;

import com.example.userservicedec24.exceptions.UnAuthorizedException;
import com.example.userservicedec24.exceptions.UserNotFoundException;
import com.example.userservicedec24.models.Token;
import com.example.userservicedec24.models.User;
import com.example.userservicedec24.repositories.TokenRepository;
import com.example.userservicedec24.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private TokenRepository tokenRepository;

    public UserServiceImpl(UserRepository userRepository,
                           TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            // redirect to login
            return optionalUser.get();
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        //TODO: We should store the password in the encoded format using BCryptPassword Encoder.
        user.setPassword(password);

        return userRepository.save(user);
    }

    @Override
    public Token login(String email, String password) throws UserNotFoundException, UnAuthorizedException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            //redired to signUp page.
            throw new UserNotFoundException("User with email: " + email + " not found.");
        }

        User user = optionalUser.get();

        if (user.getPassword().equals(password)) {
            //login successful, create the token.
            Token token = new Token();
            token.setUser(user);
            token.setValue("jslkfjklfjljoe89374njnfkjnjk1233n4kjn");

            Date currentDate = new Date(); // current date and time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);

            // Add 30 days to the calendar
            calendar.add(Calendar.DAY_OF_MONTH, 30);

            // Get the updated time as a Date object
            Date dateAfter30Days = calendar.getTime();

            token.setExpiryDate(dateAfter30Days);

            return tokenRepository.save(token);
        }

        //Login failed.
        throw new UnAuthorizedException("Login failed");
    }

    @Override
    public User validateToken(String tokenValue) {
        return null;
    }

    @Override
    public void logout(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValue(tokenValue);

        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Token Invalid.");
        }

        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }
}
